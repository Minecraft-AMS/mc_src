/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import com.google.common.collect.Lists;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class Ping {
    public static List<RegionPingResult> ping(Region ... regions) {
        for (Region region : regions) {
            Ping.ping(region.endpoint);
        }
        ArrayList list = Lists.newArrayList();
        for (Region region2 : regions) {
            list.add(new RegionPingResult(region2.name, Ping.ping(region2.endpoint)));
        }
        list.sort(Comparator.comparingInt(RegionPingResult::getPing));
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int ping(String host) {
        int i = 700;
        long l = 0L;
        Socket socket = null;
        for (int j = 0; j < 5; ++j) {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(host, 80);
                socket = new Socket();
                long m = Ping.now();
                socket.connect(socketAddress, 700);
                l += Ping.now() - m;
                Ping.close(socket);
                continue;
            }
            catch (Exception exception) {
                l += 700L;
                continue;
            }
            finally {
                Ping.close(socket);
            }
        }
        return (int)((double)l / 5.0);
    }

    private static void close(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static long now() {
        return Util.getMeasuringTimeMs();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return Ping.ping(Region.values());
    }

    @Environment(value=EnvType.CLIENT)
    static final class Region
    extends Enum<Region> {
        public static final /* enum */ Region US_EAST_1 = new Region("us-east-1", "ec2.us-east-1.amazonaws.com");
        public static final /* enum */ Region US_WEST_2 = new Region("us-west-2", "ec2.us-west-2.amazonaws.com");
        public static final /* enum */ Region US_WEST_1 = new Region("us-west-1", "ec2.us-west-1.amazonaws.com");
        public static final /* enum */ Region EU_WEST_1 = new Region("eu-west-1", "ec2.eu-west-1.amazonaws.com");
        public static final /* enum */ Region AP_SOUTHEAST_1 = new Region("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com");
        public static final /* enum */ Region AP_SOUTHEAST_2 = new Region("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com");
        public static final /* enum */ Region AP_NORTHEAST_1 = new Region("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com");
        public static final /* enum */ Region SA_EAST_1 = new Region("sa-east-1", "ec2.sa-east-1.amazonaws.com");
        final String name;
        final String endpoint;
        private static final /* synthetic */ Region[] field_19575;

        public static Region[] values() {
            return (Region[])field_19575.clone();
        }

        public static Region valueOf(String name) {
            return Enum.valueOf(Region.class, name);
        }

        private Region(String name, String endpoint) {
            this.name = name;
            this.endpoint = endpoint;
        }

        private static /* synthetic */ Region[] method_36845() {
            return new Region[]{US_EAST_1, US_WEST_2, US_WEST_1, EU_WEST_1, AP_SOUTHEAST_1, AP_SOUTHEAST_2, AP_NORTHEAST_1, SA_EAST_1};
        }

        static {
            field_19575 = Region.method_36845();
        }
    }
}

