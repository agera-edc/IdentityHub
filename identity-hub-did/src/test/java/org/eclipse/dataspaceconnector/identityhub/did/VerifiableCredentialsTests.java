package org.eclipse.dataspaceconnector.identityhub.did;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.danubetech.verifiablecredentials.jwt.FromJwtConverter;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.ToJwtConverter;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import foundation.identity.jsonld.JsonLDUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class VerifiableCredentialsTests {

    @Test
    public void verifiableCredentialsTest() throws DecoderException, JOSEException, ParseException {

        Map<String, Object> claims = new LinkedHashMap<>();
        Map<String, Object> degree = new LinkedHashMap<String, Object>();
        degree.put("name", "Bachelor of Science and Arts");
        degree.put("type", "BachelorDegree");
        claims.put("college", "Test University");
        claims.put("degree", degree);

        CredentialSubject credentialSubject = CredentialSubject.builder()
                .id(URI.create("did:example:ebfeb1f712ebc6f1c276e12ec21"))
                .claims(claims)
                .build();

        VerifiableCredential verifiableCredential = VerifiableCredential.builder()
                .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_EXAMPLES_V1)
                .type("UniversityDegreeCredential")
                .id(URI.create("http://example.edu/credentials/3732"))
                .issuer(URI.create("did:example:76e12ec712ebc6f1c221ebfeb1f"))
                .issuanceDate(JsonLDUtils.stringToDate("2019-06-16T18:56:59Z"))
                .expirationDate(JsonLDUtils.stringToDate("2019-06-17T18:56:59Z"))
                .credentialSubject(credentialSubject)
                .build();

        RSAKey jwk = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
                .keyID(UUID.randomUUID().toString()) // give the key a unique ID
                .generate();

        RSAKey publicJwk = jwk.toPublicJWK();

        JwtVerifiableCredential jwtVerifiableCredential = ToJwtConverter.toJwtVerifiableCredential(verifiableCredential);

        String jwtPayload = jwtVerifiableCredential.getPayload().toString();
        System.out.println(jwtPayload);
        String jwtString = jwtVerifiableCredential.sign_RSA_PS256(jwk);
        extract(jwtString, publicJwk);
    }

    private void extract(String jwtString, RSAKey publicJwk) throws DecoderException, JOSEException, ParseException {
        var jwtVerifiableCredential = JwtVerifiableCredential.fromCompactSerialization(jwtString);
        String jwtPayloadVerifiableCredential = jwtVerifiableCredential.getPayloadObject().toJson(true);

        System.out.println(jwtPayloadVerifiableCredential);
        System.out.println(jwtVerifiableCredential.verify_RSA_PS256(publicJwk));

        VerifiableCredential verifiableCredential = FromJwtConverter.fromJwtVerifiableCredential(jwtVerifiableCredential);
        System.out.println(verifiableCredential.toJson(true));
    }
}
