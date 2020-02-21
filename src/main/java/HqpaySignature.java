import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class HqpaySignature {

    public static String sign(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(getPrivateKey(privateKey));
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(signature.sign()).replaceAll("[\n\r]", "");
    }

    public static String sign(Map<String, Object> data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        return sign(createLinkString(data), privateKey);
    }

    private static String createLinkString(Map<String, ?> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }
        return prestr.toString();
    }

    private static PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String regex = "(-+BEGIN (RSA )?PRIVATE KEY-+\\r?\\n|-+END (RSA )?PRIVATE KEY-+\\r?\\n?)";
        KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.replaceAll(regex, "")));
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
