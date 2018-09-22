package com.github.transmitter.api;

import com.github.transmitter.model.TransferResponse;
import com.github.transmitter.model.DepositDTO;
import com.github.transmitter.model.WithdrawDTO;
import com.github.transmitter.service.ExceptionHandler;
import com.github.transmitter.service.TransferService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static com.github.transmitter.model.TransferResponse.success;

@Path("/transfer")
@ApplicationScoped
public class TransferEndpoint {
    private static final String SUCCESS_MESSAGE_MASK = "Money transfer has been finished successfully from id %s to id %s";

    @Inject
    private TransferService transferService;
    @Inject
    private ExceptionHandler exceptionHandler;

    @GET
    @Produces(value = "application/json")
    public Response transfer(@Valid
                             @NotNull(message = "'idFrom' parameter must exist")
                             @QueryParam("from") Long idFrom,
                             @Valid
                             @NotNull(message = "'idTo' parameter must exist")
                             @QueryParam("to") Long idTo,
                             @Valid
                             @Digits(integer = 19, fraction = 4, message = "amount value is not valid")
                             @DecimalMin(value = "0", message = "amount value must be more then 0")
                             @NotNull(message = "amount parameter must exist")
                             @QueryParam("amount") BigDecimal amount) {
        TransferResponse response;
        try {
            WithdrawDTO withdraw = new WithdrawDTO(idFrom, amount);
            DepositDTO deposit = new DepositDTO(idTo, amount);

            transferService.transfer(withdraw, deposit);
            response = success(createSuccessMessage(idFrom.toString(), idTo.toString()));
        } catch (Exception e) {
            response = exceptionHandler.handle(e);
        }
        return Response.status(
                response.getStatus())
                .entity(response)
                .build();
    }

    private String createSuccessMessage(String from, String to) {
        return String.format(SUCCESS_MESSAGE_MASK, from, to);
    }
}
