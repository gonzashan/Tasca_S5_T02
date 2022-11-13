package com.tasca_s5_02_n1.joc_de_daus.model.service;

import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.TokenInfo;

import java.text.ParseException;


public class TokenService {

    private static final String KEYTOKEN = "12345678901234567890123456789012";

    public static String generateToken(String idPlayer) throws JOSEException {

        Gson gson = new Gson();

        TokenInfo tokenInfo = new TokenInfo(idPlayer, System.currentTimeMillis(),
                System.currentTimeMillis() + 3600000);

        String json = gson.toJson(tokenInfo);

        byte[] sharedSecret = KEYTOKEN.getBytes();


// Create HMAC signer
        JWSSigner signer = new MACSigner(sharedSecret);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(json));

// Apply the HMAC
        jwsObject.sign(signer);

// To serialize to compact form, produces something like
        String s = jwsObject.serialize();

        return s;
    }

    public static boolean checkAuthorized(String token, String idPlayer) {

        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(token);

            byte[] sharedSecret = KEYTOKEN.getBytes();
            JWSVerifier verifier = new MACVerifier(sharedSecret);

            if (jwsObject.verify(verifier)) {

                if (!idPlayer.equals("-1") && !idPlayer.equals(jwsObject.getPayload().toJSONObject().get("idPlayer") )) {
                    return false;
                }
                if( (Long) jwsObject.getPayload().toJSONObject().get("expiresAt") < System.currentTimeMillis())
                    return false;

                return true;

            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return false;
    }
}