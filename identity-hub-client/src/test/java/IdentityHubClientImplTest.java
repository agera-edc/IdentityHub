import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.github.javafaker.Faker;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.identityhub.client.IdentityHubClientImpl;
import org.eclipse.dataspaceconnector.identityhub.models.MessageResponseObject;
import org.eclipse.dataspaceconnector.identityhub.models.MessageStatus;
import org.eclipse.dataspaceconnector.identityhub.models.RequestStatus;
import org.eclipse.dataspaceconnector.identityhub.models.ResponseObject;
import org.eclipse.dataspaceconnector.identityhub.models.credentials.VerifiableCredential;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.dataspaceconnector.identityhub.models.MessageResponseObject.MESSAGE_ID_VALUE;

public class IdentityHubClientImplTest {
    private static final String HUB_URL = "https://dummy/";
    private static final Faker FAKER = new Faker();
    private static final String VERIFIABLE_CREDENTIAL_ID = FAKER.internet().uuid();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void getVerifiableCredentials() throws IOException {
        VerifiableCredential credential = VerifiableCredential.Builder.newInstance().id(VERIFIABLE_CREDENTIAL_ID).build();

        Interceptor interceptor = chain -> {
            Request request = chain.request();
            var replies = MessageResponseObject.Builder.newInstance().messageId(MESSAGE_ID_VALUE)
                    .status(MessageStatus.OK).entries(List.of(credential)).build();
            var responseObject = ResponseObject.Builder.newInstance()
                    .requestId(FAKER.internet().uuid())
                    .status(RequestStatus.OK)
                    .replies(List.of(replies))
                    .build();
            var body = ResponseBody.create(OBJECT_MAPPER.writeValueAsString(responseObject), MediaType.get("application/json"));

            Response response = new Response.Builder()
                    .body(body)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(200)
                    .message("")
                    .build();
            return response;
        };

        var client = createClient(interceptor);
        var credentials = client.getVerifiableCredentials(HUB_URL);
        assertThat(credentials).usingRecursiveFieldByFieldElementComparator().containsExactly(credential);
    }

    @Test
    void getVerifiableCredentialsServerError() throws IOException {

        Interceptor interceptor = chain -> {
            Request request = chain.request();
            var body = ResponseBody.create("{}", MediaType.get("application/json"));
            Response response = new Response.Builder()
                    .body(body)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(500)
                    .message("")
                    .build();
            return response;
        };

        var client = createClient(interceptor);

        assertThatThrownBy(() -> client.getVerifiableCredentials(HUB_URL)).isInstanceOf(ServerException.class);
    }

    @Test
    void getVerifiableCredentialsDeserializationError() throws IOException {
        Interceptor interceptor = chain -> {
            Request request = chain.request();
            var body = ResponseBody.create("{}", MediaType.get("application/json"));

            Response response = new Response.Builder()
                    .body(body)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(200)
                    .message("{}")
                    .build();
            return response;
        };

        var client = createClient(interceptor);
        assertThatThrownBy(() -> client.getVerifiableCredentials(HUB_URL)).isInstanceOf(ValueInstantiationException.class);
    }

    private IdentityHubClientImpl createClient(Interceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        return new IdentityHubClientImpl(okHttpClient, OBJECT_MAPPER);
    }
}
