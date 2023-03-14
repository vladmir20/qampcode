package com.qamp.app.qampcallss.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortUtils {
    private static final String TAG = "SortUtils";

    public static class ViewComparator implements Comparator<Object> {
        private MesiboCall.MesiboParticipantSortListener mListener = null;

        ViewComparator(MesiboCall.MesiboParticipantSortListener mesiboParticipantSortListener) {
            this.mListener = mesiboParticipantSortListener;
        }

        public int compare(Object obj, Object obj2) {
            MesiboCall.MesiboParticipant ParticipantSort_onGetParticipant = this.mListener.ParticipantSort_onGetParticipant(obj);
            MesiboCall.MesiboParticipant ParticipantSort_onGetParticipant2 = this.mListener.ParticipantSort_onGetParticipant(obj2);
            if (!ParticipantSort_onGetParticipant.isMe() && ParticipantSort_onGetParticipant2.isMe()) {
                return -1;
            }
            if (ParticipantSort_onGetParticipant.isMe() && !ParticipantSort_onGetParticipant2.isMe()) {
                return 1;
            }
            if (ParticipantSort_onGetParticipant.getTalkTimestamp() > ParticipantSort_onGetParticipant2.getTalkTimestamp()) {
                return -1;
            }
            if (ParticipantSort_onGetParticipant.getTalkTimestamp() < ParticipantSort_onGetParticipant2.getTalkTimestamp()) {
                return 1;
            }
            if (ParticipantSort_onGetParticipant.hasVideo() && !ParticipantSort_onGetParticipant2.hasVideo()) {
                return -1;
            }
            if (ParticipantSort_onGetParticipant.hasVideo() || !ParticipantSort_onGetParticipant2.hasVideo()) {
                return ParticipantSort_onGetParticipant.getName().compareTo(ParticipantSort_onGetParticipant2.getName());
            }
            return 1;
        }
    }

    private static Object getNext(MesiboCall.MesiboParticipantSortListener mesiboParticipantSortListener, ArrayList<? extends Object> arrayList, int i, boolean z) {
        if (arrayList == null) {
            return null;
        }
        while (i < arrayList.size()) {
            Object obj = arrayList.get(i);
            if (obj == null || !(mesiboParticipantSortListener.ParticipantSort_onGetParticipant(obj).isVideoLandscape() ^ z)) {
                i++;
            } else {
                arrayList.remove(i);
                return obj;
            }
        }
        return null;
    }

    protected static ArrayList<? extends Object> sortStreams(MesiboCall.MesiboParticipantSortListener mesiboParticipantSortListener, ArrayList<? extends Object> arrayList, float f, float f2, int i, int i2, MesiboCall.MesiboParticipantSortParams mesiboParticipantSortParams) {
        int i3;
        int i4;
        MesiboCall.MesiboParticipant ParticipantSort_onGetParticipant;
        if (mesiboParticipantSortParams == null) {
            mesiboParticipantSortParams = new MesiboCall.MesiboParticipantSortParams();
        }
        ArrayList arrayList2 = new ArrayList(arrayList);
        Collections.sort(arrayList2, new ViewComparator(mesiboParticipantSortListener));
        int i5 = 0;
        int i6 = 0;
        int size = arrayList2.size();
        float f3 = 0.0f;
        float f4 = 0.0f;
        for (int i7 = 0; i7 < arrayList2.size(); i7++) {
            Object obj = arrayList2.get(i7);
            if (!(obj == null || (ParticipantSort_onGetParticipant = mesiboParticipantSortListener.ParticipantSort_onGetParticipant(obj)) == null)) {
                float aspectRatio = ParticipantSort_onGetParticipant.getAspectRatio();
                if (ParticipantSort_onGetParticipant.isVideoLandscape()) {
                    i6++;
                    if (f4 == 0.0f || aspectRatio > f4) {
                        f4 = aspectRatio;
                    }
                } else {
                    i5++;
                    if (f3 == 0.0f || aspectRatio < f3) {
                        f3 = aspectRatio;
                    }
                }
            }
        }
        if (((double) mesiboParticipantSortParams.maxHorzAspectRatio) > 0.9999d && f4 > mesiboParticipantSortParams.maxHorzAspectRatio) {
            f4 = mesiboParticipantSortParams.maxHorzAspectRatio;
        }
        float f5 = (((double) mesiboParticipantSortParams.minVertAspectRation) <= 0.2d || f3 >= mesiboParticipantSortParams.minVertAspectRation) ? f3 : mesiboParticipantSortParams.minVertAspectRation;
        if (f4 < 1.0f) {
            f4 = 1.0f;
        }
        if (f5 > 1.0f || ((double) f5) < 0.1d) {
            f5 = 1.0f;
        }
        float f6 = 1.0f / f4;
        float f7 = 1.0f / f5;
        new StringBuilder("Arrange ").append(size).append(" videos  nV: ").append(i5).append(" nH: ").append(i6);
        int i8 = size < 4 ? i5 : i5 & 1;
        int i9 = size < 4 ? i6 : i6 & 1;
        int i10 = (i5 - i8) / 2;
        int i11 = (i6 - i9) / 2;
        if (4 == size && 2 == i5) {
            i8 = 0;
            i10 = 1;
            i9 = 2;
            i11 = 0;
        }
        if (3 != size || i5 <= 2) {
            i3 = i9;
            i4 = i8;
        } else {
            i4 = 0;
            i10 = 1;
            i11 = 0;
            i3 = 1;
        }
        float f8 = 0.0f;
        if (i4 > 0) {
            f8 = 0.0f + (((float) i4) * f7);
        }
        if (i3 > 0) {
            f8 += ((float) i3) * f6;
        }
        if (i10 > 0) {
            f8 += (((float) i10) * f7) / 2.0f;
        }
        if (i11 > 0) {
            f8 += (((float) i11) * f6) / 2.0f;
        }
        float f9 = (f2 * f7) / f8;
        float f10 = (f2 * f6) / f8;
        float f11 = f9 / 2.0f;
        float f12 = f10 / 2.0f;
        ArrayList arrayList3 = new ArrayList(size);
        int i12 = 0;
        float f13 = 0.0f;
        int i13 = i10;
        int i14 = i11;
        while (arrayList2.size() > 0) {
            Object obj2 = arrayList2.get(0);
            arrayList2.remove(0);
            Object obj3 = null;
            boolean isVideoLandscape = mesiboParticipantSortListener.ParticipantSort_onGetParticipant(obj2).isVideoLandscape();
            if ((isVideoLandscape && i14 > 0) || (!isVideoLandscape && i13 > 0)) {
                obj3 = getNext(mesiboParticipantSortListener, arrayList2, 0, !isVideoLandscape);
            }
            if (obj2 == null || obj3 == null) {
                float f14 = isVideoLandscape ? f10 : f9;
                mesiboParticipantSortListener.ParticipantSort_onSetCoordinates(obj2, i12, 0.0f, f13, f, f14);
                arrayList3.add(obj2);
                f13 += f14;
                i12++;
            } else {
                float f15 = isVideoLandscape ? f12 : f11;
                float f16 = f / 2.0f;
                mesiboParticipantSortListener.ParticipantSort_onSetCoordinates(obj2, i12, 0.0f, f13, f16, f15);
                arrayList3.add(obj2);
                mesiboParticipantSortListener.ParticipantSort_onSetCoordinates(obj3, i12 + 1, f16, f13, f16, f15);
                arrayList3.add(obj3);
                f13 += f15;
                i12 += 2;
                if (isVideoLandscape) {
                    i14--;
                } else {
                    i13--;
                }
            }
        }
        return arrayList3;
    }
}
