package com.qamp.app.qampCalls;



import com.mesibo.api.Mesibo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

public class SdpInterface {
    public SdpInterface() {
    }

    public static void onSdp(String var0, RtcCall var1) {
        try {
            String var2;
            JSONObject var5;
            if ((var2 = (var5 = new JSONObject(var0)).optString("type")).equals("candidate")) {
                var1.addRemoteIceCandidate(toJavaCandidate(var5));
            } else if (!var2.equals("remove-candidates")) {
                SessionDescription var7;
                if (var2.equals("answer")) {
                    var7 = new SessionDescription(Type.fromCanonicalForm(var2), var5.getString("sdp"));
                    var1.setRemoteDescription(var7);
                } else if (var2.equals("offer")) {
                    var7 = new SessionDescription(Type.fromCanonicalForm(var2), var5.getString("sdp"));
                    var1.setRemoteDescription(var7);
                    var1.createAnswer();
                }
            } else {
                JSONArray var6;
                IceCandidate[] var8 = new IceCandidate[(var6 = var5.getJSONArray("candidates")).length()];

                for(int var3 = 0; var3 < var6.length(); ++var3) {
                    var8[var3] = toJavaCandidate(var6.getJSONObject(var3));
                }

                var1.removeRemoteIceCandidates(var8);
            }
        } catch (JSONException var4) {
        }
    }

    private static void jsonPut(JSONObject var0, String var1, Object var2) {
        try {
            var0.put(var1, var2);
        } catch (JSONException var3) {
            throw new RuntimeException(var3);
        }
    }

    private static void send(JSONObject var0) {
        Mesibo.setCallStatus(8, var0.toString());
    }

    public static void sendSdp(SessionDescription var0, boolean var1) {
        JSONObject var2;
        jsonPut(var2 = new JSONObject(), "sdp", var0.description);
        jsonPut(var2, "type", var1 ? "answer" : "offer");
        send(var2);
    }

    private static JSONObject toJsonCandidate(IceCandidate var0) {
        JSONObject var1;
        jsonPut(var1 = new JSONObject(), "label", var0.sdpMLineIndex);
        jsonPut(var1, "id", var0.sdpMid);
        jsonPut(var1, "candidate", var0.sdp);
        return var1;
    }

    public static IceCandidate toJavaCandidate(JSONObject var0) {//==============
        try {
            return new IceCandidate(var0.getString("id"), var0.getInt("label"), var0.getString("candidate"));
        } catch (Exception e) {
            return new IceCandidate("",0,"");
        }
    }

    public static void sendLocalIceCandidate(IceCandidate var0) {
        JSONObject var1;
        jsonPut(var1 = new JSONObject(), "type", "candidate");
        jsonPut(var1, "label", var0.sdpMLineIndex);
        jsonPut(var1, "id", var0.sdpMid);
        jsonPut(var1, "candidate", var0.sdp);
        send(var1);
    }

    public static void sendLocalIceCandidateRemovals(IceCandidate[] var0) {
        JSONObject var1;
        jsonPut(var1 = new JSONObject(), "type", "remove-candidates");
        JSONArray var2 = new JSONArray();
        int var3 = var0.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            IceCandidate var5 = var0[var4];
            var2.put(toJsonCandidate(var5));
        }

        jsonPut(var1, "candidates", var2);
        send(var1);
    }
}

