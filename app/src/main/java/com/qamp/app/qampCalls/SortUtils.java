package com.qamp.app.qampCalls;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortUtils {
    private static final String TAG = "SortUtils";

    public SortUtils() {
    }

    private static Object getNext(MesiboCall.MesiboParticipantSortListener var0, ArrayList<? extends Object> var1, int var2, boolean var3) {
        if (var1 == null) {
            return null;
        } else {
            while(var2 < var1.size()) {
                Object var4;
                if ((var4 = var1.get(var2)) != null && var3 ^ var0.ParticipantSort_onGetParticipant(var4).isVideoLandscape()) {
                    var1.remove(var2);
                    return var4;
                }

                ++var2;
            }

            return null;
        }
    }

    protected static ArrayList<? extends Object> sortStreams(MesiboCall.MesiboParticipantSortListener var0, ArrayList<? extends Object> var1, float var2, float var3, int var4, int var5, MesiboCall.MesiboParticipantSortParams var6) {
        if (var6 == null) {
            var6 = new MesiboCall.MesiboParticipantSortParams();
        }
        var1 = new ArrayList(var1);
        Collections.sort(var1, new ViewComparator(var0));
        var4 = 0;
        var5 = 0;
        int var7 = var1.size();
        float var8 = 0.0F;
        float var9 = 0.0F;

        int var10;
        for(var10 = 0; var10 < var1.size(); ++var10) {
            Object var11;
            MesiboCall.MesiboParticipant var12;
            if ((var11 = var1.get(var10)) != null && (var12 = var0.ParticipantSort_onGetParticipant(var11)) != null) {
                float var13 = var12.getAspectRatio();
                if (var12.isVideoLandscape()) {
                    ++var5;
                    if (var9 == 0.0F || var13 > var9) {
                        var9 = var13;
                    }
                } else {
                    ++var4;
                    if (var8 == 0.0F || var13 < var8) {
                        var8 = var13;
                    }
                }
            }
        }

        if ((double)var6.maxHorzAspectRatio > 0.9999D && var9 > var6.maxHorzAspectRatio) {
            var9 = var6.maxHorzAspectRatio;
        }

        if ((double)var6.minVertAspectRation > 0.2D && var8 < var6.minVertAspectRation) {
            var8 = var6.minVertAspectRation;
        }

        if (var9 < 1.0F) {
            var9 = 1.0F;
        }

        if (var8 > 1.0F || (double)var8 < 0.1D) {
            var8 = 1.0F;
        }

        float var20 = 1.0F / var9;
        float var21 = 1.0F / var8;
        (new StringBuilder("Arrange ")).append(var7).append(" videos  nV: ").append(var4).append(" nH: ").append(var5);
        int var22 = var7 < 4 ? var4 : var4 & 1;
        int var23 = var7 < 4 ? var5 : var5 & 1;
        int var18 = (var4 - var22) / 2;
        var5 = (var5 - var23) / 2;
        if (4 == var7 && 2 == var4) {
            var22 = 0;
            var18 = 1;
            var23 = 2;
            var5 = 0;
        }

        if (3 == var7 && var4 > 2) {
            var22 = 0;
            var18 = 1;
            var23 = 1;
            var5 = 0;
        }

        float var17 = 0.0F;
        if (var22 > 0) {
            var17 = 0.0F + var21 * (float)var22;
        }

        if (var23 > 0) {
            var17 += var20 * (float)var23;
        }

        if (var18 > 0) {
            var17 += var21 * (float)var18 / 2.0F;
        }

        if (var5 > 0) {
            var17 += var20 * (float)var5 / 2.0F;
        }

        var8 = var3 * var21 / var17;
        var3 = var3 * var20 / var17;
        var17 = var8 / 2.0F;
        var9 = var3 / 2.0F;
        ArrayList var19 = new ArrayList(var7);
        var10 = 0;
        var21 = 0.0F;

        while(true) {
            while(var1.size() > 0) {
                Object var24 = var1.get(0);
                var1.remove(0);
                Object var25 = null;
                boolean var14;
                if ((var14 = var0.ParticipantSort_onGetParticipant(var24).isVideoLandscape()) && var5 > 0 || !var14 && var18 > 0) {
                    var25 = getNext(var0, var1, 0, !var14);
                }

                float var15;
                if (var24 != null && var25 != null) {
                    var15 = var14 ? var9 : var17;
                    float var16 = var2 / 2.0F;
                    var0.ParticipantSort_onSetCoordinates(var24, var10, 0.0F, var21, var16, var15);
                    var19.add(var24);
                    var0.ParticipantSort_onSetCoordinates(var25, var10 + 1, var16, var21, var16, var15);
                    var19.add(var25);
                    var21 += var15;
                    var10 += 2;
                    if (var14) {
                        --var5;
                    } else {
                        --var18;
                    }
                } else {
                    var15 = var14 ? var3 : var8;
                    var0.ParticipantSort_onSetCoordinates(var24, var10, 0.0F, var21, var2, var15);
                    var19.add(var24);
                    var21 += var15;
                    ++var10;
                }
            }

            return var19;
        }
    }

    public static class ViewComparator implements Comparator<Object> {
        private MesiboCall.MesiboParticipantSortListener mListener = null;

        ViewComparator(MesiboCall.MesiboParticipantSortListener var1) {
            this.mListener = var1;
        }

        public int compare(Object var1, Object var2) {
            MesiboCall.MesiboParticipant var3 = this.mListener.ParticipantSort_onGetParticipant(var1);
            MesiboCall.MesiboParticipant var4 = this.mListener.ParticipantSort_onGetParticipant(var2);
            if (!var3.isMe() && var4.isMe()) {
                return -1;
            } else if (var3.isMe() && !var4.isMe()) {
                return 1;
            } else if (var3.getTalkTimestamp() > var4.getTalkTimestamp()) {
                return -1;
            } else if (var3.getTalkTimestamp() < var4.getTalkTimestamp()) {
                return 1;
            } else if (var3.hasVideo() && !var4.hasVideo()) {
                return -1;
            } else {
                return !var3.hasVideo() && var4.hasVideo() ? 1 : var3.getName().compareTo(var4.getName());
            }
        }
    }
}

