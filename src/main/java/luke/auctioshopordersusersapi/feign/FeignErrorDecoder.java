package luke.auctioshopordersusersapi.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 401:
                return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                        "Błąd autentykacji. Spróbuj zalogować się ponownie");
            case 403:
                return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                        "Nie masz uprawnień dostępu do tych zasobów serwisu Produkty");
            case 404:
                return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                        "Nie udało połączyć się z serwisem Produkty. Spróbuj ponownie później.");
            default:
                return new Exception(response.reason());
        }
    }
}
