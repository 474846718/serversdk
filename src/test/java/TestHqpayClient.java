import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.*;

@RunWith(JUnit4.class)
public class TestHqpayClient {

    private static final String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANRtA2FZXiaFlmx6\n" +
            "pOvNdIidgIWU8aL1ZwC7FS+IaWhdUO4DMaQOBREA+dIcH3fTAM2vp+1VjhQligSM\n" +
            "qv0MIGz4gjLgjBWVotHwh9gVO2O8X5Fazquqw87WbCo+aopIda1uhLDUreUUg+o7\n" +
            "kEBfiObzW5SE6yLZ8b9MAPNpURm/AgMBAAECgYBdt05jd1rbIdpwN1dlFHpR/zKB\n" +
            "PGmyXBO6Nc8t7j+apHic2Nngp7LCgqy0bmXPpk5XcCRkx3bsGneedDTHeL8gphCs\n" +
            "ImFofmrYMGtUmLxi8aAZYWrfsA6fx0cZ7zJC0VCdMp8KEBe+JKaE7fGKF9wfFjMp\n" +
            "hTcb9wCzWdc69GLzuQJBAPdCKWgCkEu7Nfc5Voow9lVCyt2vVLBbc2m2K1rbQp59\n" +
            "fdLa2FLl6u1Ltmz7dzQLcf72AHrcrVX/3jcw0YdNhU0CQQDb75hk4E0g3N0jV3CR\n" +
            "sv7XRsC4WruCPseNaQ0daTT8RhkSeaWDxmqvmlz5gsRt67xaCAcfRjiLdMRYRINa\n" +
            "TGU7AkAP4D+oXgHF2w9sAFJ+LQakEtXTxh459KVEdF30R3/PjV822rZpHDopYmMk\n" +
            "PEE7JkD7jfAZ83FtxDAQeyH4/hFBAkEAwuCb8dZlwRayw1v5HMCz2mCQ50lOGGw0\n" +
            "OXK58jOEhXw/PwWWyRqI8awfcd6S2qskq8mtNeLsu3TtfSDIGrzSfQJAAnj1LGFF\n" +
            "YHsUQp8/Qs0COxwwi2Kgkc6OsAHvAmiViRgmEnQljRikQz/obWN05ztdvV4Pw9US\n" +
            "MtS7fXG7qzpqWA==\n" +
            "-----END PRIVATE KEY-----\n";

    private static final String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUbQNhWV4mhZZseqTrzXSInYCF\n" +
            "lPGi9WcAuxUviGloXVDuAzGkDgURAPnSHB930wDNr6ftVY4UJYoEjKr9DCBs+IIy\n" +
            "4IwVlaLR8IfYFTtjvF+RWs6rqsPO1mwqPmqKSHWtboSw1K3lFIPqO5BAX4jm81uU\n" +
            "hOsi2fG/TADzaVEZvwIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";

    private HqpayClient hqpayClient;

    @Before
    public void init() {
        this.hqpayClient = new HqpayClient("1819", "secret", privateKey);
        this.hqpayClient.setHost("http://jicheng.upaypal.cn");
    }


    @Test
    public void testTrade() throws Exception {
        /* 1起始单issuer_id一定要是appid关联的商户id，
         *  2 订单之间issuer_id和receiver_id一定要是链式关系
         *  3 分销单号要一致，订单单号要不同，同时在系统中不存在
         */
        List<Map<String, Object>> dis_invoices = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("order_type", "dis_trade");
                    put("issuer_id", 1819);
                    put("receiver_id", 277);
                    put("dis_trade_no", "8974894789454458");
                    put("out_trade_no", "8974894789454455_958");
                    put("amount", 0.01);
                    put("subject", "YourSubject");
                    put("time_expire", "2020-12-12 00:00:00");
                }}
        );
        System.out.println("创建分销交易单:" + hqpayClient.executeForList(dis_invoices, HqpayResource.TRADE_PATH, "POST"));
        Thread.sleep(1000);
//
//
//        Map<String, Object> trade_invoice = new HashMap<String, Object>() {{
//            put("receiver_id", 1819);
//            put("order_type", "trade");
//            put("channel", "upacp_app");
//            put("out_trade_no", "8974894789454455_999");
//            put("amount", 0.01);
//            put("subject", "YourSubject");
//            put("time_expire", "2020-12-12 00:00:00");
//        }};
//        System.out.println("创建交易单:" + hqpayClient.executeForList(Collections.singletonList(trade_invoice), HqpayResource.TRADE_PATH, "POST"));
//        Thread.sleep(1000);
//
//
//        Map<String, Object> get_invoice = new HashMap<String, Object>() {{
//            put("out_trade_no", "8974894789454455_999");
//        }};
//        System.out.println("查询订单:" + hqpayClient.executeForOne(get_invoice, HqpayResource.TRADE_PATH, "GET"));
    }

    @Test
    public void testRefund() throws Exception {
        Map<String, Object> refund = new HashMap<String, Object>() {{
            put("out_refund_no", "8974894789454488124");
            put("out_trade_no", "2020021936900100043_1230020293364547584");
            put("refund_amount", "0.01");
            put("refund_reason", "refund_reason");
        }};
        System.out.println("创建退款单:" + hqpayClient.executeForOne(refund, HqpayResource.REFUNDE_PATH, "POST"));
        Thread.sleep(1000);

//        Map<String, Object> get_refund = new HashMap<String, Object>() {{
//            put("out_refund_no", "8974894789454488123");
//        }};
//        System.out.println("查询退款单:" + hqpayClient.executeForOne(get_refund, HqpayResource.REFUNDE_PATH, "GET"));
    }

    @Test
    public void testBalancePay() throws Exception {
        Map<String, Object> balancePay = new HashMap<String, Object>() {{
            put("out_trade_no", "8974894789454455_958");
            put("distribution_out_trade_no", "8974894789454458");
            put("amount", 0.01);
            put("app_id", 1819);
        }};
        System.out.println("查询订单:" + hqpayClient.executeForOne(balancePay, HqpayResource.BALANCE_PAY_PATH, "POST"));

    }

    @Test
    public void testCancel() throws Exception {
        Map<String, Object> cancel = new HashMap<String, Object>() {{
            put("cancel_reason", "");
            put("marketplace_id", "");
            put("out_trade_no", "");
        }};
        System.out.println("查询订单:" + hqpayClient.executeForOne(cancel, HqpayResource.CANCEL_PATH, "PUT"));
    }

    @Test
    public void testSign() throws Exception {
        List<Map<String, Object>> pMaps =
                Arrays.asList(
                        new HashMap<String, Object>() {{
                            put("order_type", "dis_trade");
                            put("issuer_id", 1819);
                            put("receiver_id", 6213);
                            put("dis_trade_no", "8974894789454455");
                            put("out_trade_no", "8974894789454455_811");
                            put("channel", "upacp_app");
                            put("amount", 0.01);
                            put("subject", "YourSubject");
                            put("time_expire", "2020-12-12 00:00:00");
                        }}, new HashMap<String, Object>() {{
                            put("order_type", "dis_trade");
                            put("issuer_id", 6213);
                            put("receiver_id", 13199);
                            put("dis_trade_no", "8974894789454455");
                            put("out_trade_no", "8974894789454455_811");
                            put("channel", "upacp_app");
                            put("amount", 0.01);
                            put("subject", "YourSubject");
                            put("time_expire", "2020-12-12 00:00:00");
                        }}
                );

        for (Map<String, Object> map : pMaps) {
            map.put("signature", HqpaySignature.sign(map, privateKey));
        }
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pMaps));
    }

}
