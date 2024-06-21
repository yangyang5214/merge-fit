package com.yangyang5214.mergefit.fit;

import com.garmin.fit.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FitDecode {


    public FitSession session = new FitSession();

    public void Encode(String fitFile) {
        Decode decode = new Decode();
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);
        Listener listener = new Listener(session);
        FileInputStream in;

        try {
            in = new FileInputStream(fitFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        mesgBroadcaster.addListener((FileIdMesgListener) listener);
        mesgBroadcaster.addListener((UserProfileMesgListener) listener);
        mesgBroadcaster.addListener((DeviceInfoMesgListener) listener);
        mesgBroadcaster.addListener((MonitoringMesgListener) listener);
        mesgBroadcaster.addListener((RecordMesgListener) listener);
        mesgBroadcaster.addListener((LapMesgListener) listener);
        mesgBroadcaster.addListener((SessionMesgListener) listener);

        decode.read(in, mesgBroadcaster, mesgBroadcaster);

        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Decoded FIT file: " + fitFile);
        System.out.println("Record size: " + session.getRecords().size());
        System.out.println("Lap size: " + session.getLaps().size());
        System.out.println();
    }


    private static class Listener implements FileIdMesgListener, UserProfileMesgListener, DeviceInfoMesgListener,
            MonitoringMesgListener, RecordMesgListener, DeveloperFieldDescriptionListener, SessionMesgListener,
            EventMesgListener, LapMesgListener {
        private final FitSession session;

        public Listener(FitSession session) {
            this.session = session;
        }

        @Override
        public void onMesg(FileIdMesg mesg) {
            this.session.setFileIdMesg(mesg);
        }

        @Override
        public void onMesg(UserProfileMesg mesg) {
        }

        @Override
        public void onMesg(DeviceInfoMesg mesg) {
        }

        @Override
        public void onMesg(MonitoringMesg mesg) {
        }

        @Override
        public void onMesg(RecordMesg mesg) {
            session.getRecords().add(mesg);
        }

        @Override
        public void onDescription(DeveloperFieldDescription desc) {
        }

        private void printValues(Mesg mesg, int fieldNum) {
            Iterable<FieldBase> fields = mesg.getOverrideField((short) fieldNum);
            Field profileField = Factory.createField(mesg.getNum(), fieldNum);
            boolean namePrinted = false;

            for (FieldBase field : fields) {
                if (!namePrinted) {
                    System.out.println("   " + profileField.getName() + ":");
                    namePrinted = true;
                }

                if (field instanceof Field) {
                    System.out.println("      native: " + field.getValue());
                } else {
                    System.out.println("      override: " + field.getValue());
                }
            }
        }

        @Override
        public void onMesg(SessionMesg sessionMesg) {
            this.session.setSessionMesg(sessionMesg);
        }

        @Override
        public void onMesg(EventMesg eventMesg) {
            this.session.getEvents().add(eventMesg);
        }

        @Override
        public void onMesg(LapMesg lapMesg) {
            this.session.getLaps().add(lapMesg);
        }
    }
}




