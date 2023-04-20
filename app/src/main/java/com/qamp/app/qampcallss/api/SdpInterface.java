/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.qampcallss.api;

import com.mesibo.api.Mesibo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class SdpInterface {
    private static void jsonPut(JSONObject jSONObject, String str, Object obj) {
        try {
            jSONObject.put(str, obj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void onSdp(String str, RtcCall rtcCall) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String optString = jSONObject.optString("type");
            if (optString.equals("candidate")) {
                rtcCall.addRemoteIceCandidate(toJavaCandidate(jSONObject));
            } else if (optString.equals("remove-candidates")) {
                JSONArray jSONArray = jSONObject.getJSONArray("candidates");
                IceCandidate[] iceCandidateArr = new IceCandidate[jSONArray.length()];
                for (int i = 0; i < jSONArray.length(); i++) {
                    iceCandidateArr[i] = toJavaCandidate(jSONArray.getJSONObject(i));
                }
                rtcCall.removeRemoteIceCandidates(iceCandidateArr);
            } else if (optString.equals("answer")) {
                rtcCall.setRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString), jSONObject.getString("sdp")));
            } else if (optString.equals("offer")) {
                rtcCall.setRemoteDescription(new SessionDescription(SessionDescription.Type.fromCanonicalForm(optString), jSONObject.getString("sdp")));
                rtcCall.createAnswer();
            }
        } catch (JSONException e) {
        }
    }

    private static void send(JSONObject jSONObject) {
        Mesibo.setCallStatus(8, jSONObject.toString());
    }

    public static void sendLocalIceCandidate(IceCandidate iceCandidate) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "type", "candidate");
        jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
        jsonPut(jSONObject, "id", iceCandidate.sdpMid);
        jsonPut(jSONObject, "candidate", iceCandidate.sdp);
        send(jSONObject);
    }

    public static void sendLocalIceCandidateRemovals(IceCandidate[] iceCandidateArr) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "type", "remove-candidates");
        JSONArray jSONArray = new JSONArray();
        for (IceCandidate jsonCandidate : iceCandidateArr) {
            jSONArray.put(toJsonCandidate(jsonCandidate));
        }
        jsonPut(jSONObject, "candidates", jSONArray);
        send(jSONObject);
    }

    public static void sendSdp(SessionDescription sessionDescription, boolean z) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "sdp", sessionDescription.description);
        jsonPut(jSONObject, "type", z ? "answer" : "offer");
        send(jSONObject);
    }

    public static IceCandidate toJavaCandidate(JSONObject jSONObject) {
        try {
            return new IceCandidate(jSONObject.getString("id"), jSONObject.getInt("label"), jSONObject.getString("candidate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject toJsonCandidate(IceCandidate iceCandidate) {
        JSONObject jSONObject = new JSONObject();
        jsonPut(jSONObject, "label", Integer.valueOf(iceCandidate.sdpMLineIndex));
        jsonPut(jSONObject, "id", iceCandidate.sdpMid);
        jsonPut(jSONObject, "candidate", iceCandidate.sdp);
        return jSONObject;
    }
}
