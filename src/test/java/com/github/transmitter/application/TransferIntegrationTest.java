package com.github.transmitter.application;

import com.github.transmitter.JaxRsApplication;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class TransferIntegrationTest {

    @Deployment
    public static Archive createDeployment() throws Exception {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(JaxRsApplication.class)
                .addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", JaxRsApplication.class.getClassLoader()), "classes/META-INF/persistence.xml")
                .addAsWebInfResource(new ClassLoaderAsset("META-INF/load.sql", JaxRsApplication.class.getClassLoader()), "classes/META-INF/load.sql")
                .addClass(TestGetBalanceService.class)
                .addPackages(true, "com.github.transmitter");
    }

    private static Map<Long, BigDecimal> expectedBalanceState = new HashMap<>();

    @BeforeClass
    public static void setUp() {
        expectedBalanceState.put(1L, valueOf(100));
        expectedBalanceState.put(2L, ZERO);
    }

    /**
     * Positive tests
     */
    @Test
    @RunAsClient
    public void positive_transferMoney_1() throws Exception {

        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2&amount=50.5555", OK);

        BigDecimal delta = valueOf(50.5555);
        addToExpectedBalance(2L, delta);
        subtractFromExpectedBalance(1L, delta);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void positive_transferMoney_2() throws Exception {

        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=2&to=1&amount=3", OK);

        BigDecimal delta = valueOf(3);
        addToExpectedBalance(1L, delta);
        subtractFromExpectedBalance(2L, delta);

        checkBalance();
    }

    /**
     * Negative tests
     **/
    @Test
    @RunAsClient
    public void negative_withoutAmount() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_withoutFrom() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?to=2&amount=1.1", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_withoutTo() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=2&amount=1.1", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_negativeAmount() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2&amount=-1", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_invalidAmount_integers() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2&amount=123412341234123412341234", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_invalidAmount_fraction() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2&amount=123.121212", BAD_REQUEST);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_notFoundTo() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=3&amount=10", NOT_FOUND);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_notFoundBoth() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=4&to=3&amount=10", NOT_FOUND);

        checkBalance();
    }

    @Test
    @RunAsClient
    public void negative_moneyLack() throws Exception {
        sendRequestAndAssertStatus("http://localhost:8080/api/transfer?from=1&to=2&amount=150", FORBIDDEN);

        checkBalance();
    }

    private void addToExpectedBalance(Long id, BigDecimal amount) {
        expectedBalanceState.put(id,
                expectedBalanceState.get(id).add(amount));
    }

    private void subtractFromExpectedBalance(Long id, BigDecimal amount) {
        expectedBalanceState.put(id,
                expectedBalanceState.get(id).subtract(amount));
    }

    public void checkBalance() throws Exception {
        expectedBalanceState.entrySet()
                .forEach(new Consumer<Map.Entry<Long, BigDecimal>>() {
                    @Override
                    public void accept(Map.Entry<Long, BigDecimal> longBigDecimalEntry) {
                        try {
                            checkBalance(longBigDecimalEntry.getKey(), longBigDecimalEntry.getValue());
                        } catch (Exception e) {
                            throw new RuntimeException();
                        }
                    }
                });
    }

    public void checkBalance(Long id, BigDecimal expected) throws Exception {
        expected = expected.setScale(4, RoundingMode.CEILING);
        Request request = Request.Get(String.format("http://localhost:8080/api/get?id=%d", id));
        HttpResponse response = request.execute().returnResponse();
        String amount = IOUtils.toString(response.getEntity().getContent());
        assertEquals(expected, new BigDecimal(amount));
    }

    private void sendRequestAndAssertStatus(String url, Response.Status status) throws Exception {
        Request request = Request.Get(url);
        HttpResponse response = request.execute().returnResponse();
        assertTrue(response.getStatusLine().getStatusCode() == status.getStatusCode());
    }

}
