package com.yangyang5214.mergefit.fit;

import com.garmin.fit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FitMerge {


    private final String filename;
    private final List<FitSession> sessions;
    private final DateTime startTime;
    private final DateTime endTime;

    public FitMerge(String filename, List<FitSession> sessions) {
        this.filename = filename;
        this.sessions = sessions;

        this.startTime = sessions.get(0).getSession().getStartTime();

        List<RecordMesg> recordMesgs = sessions.get(sessions.size() - 1).getRecords();
        this.endTime = recordMesgs.get(recordMesgs.size() - 1).getTimestamp();

    }

    public void Merge() {
        File fileType = File.ACTIVITY;
        short manufacturerId = Manufacturer.DEVELOPMENT;
        short productId = 1;
        float softwareVersion = 1;

        int serialNumber = new Random().nextInt();

        // Every FIT file MUST contain a File ID message
        FileIdMesg fileIdMesg = new FileIdMesg();
        fileIdMesg.setType(fileType);
        fileIdMesg.setManufacturer((int) manufacturerId);
        fileIdMesg.setProduct((int) productId);
        fileIdMesg.setTimeCreated(this.sessions.get(0).getFileIdMesg().getTimeCreated());
        fileIdMesg.setSerialNumber((long) serialNumber);

        DeviceInfoMesg deviceInfoMesg = new DeviceInfoMesg();
        deviceInfoMesg.setDeviceIndex(DeviceIndex.CREATOR);
        deviceInfoMesg.setManufacturer(Manufacturer.DEVELOPMENT);
        deviceInfoMesg.setProduct((int) productId);
        deviceInfoMesg.setProductName("gpxt");
        deviceInfoMesg.setSerialNumber((long) serialNumber);
        deviceInfoMesg.setSoftwareVersion(softwareVersion);

        // Create the output stream
        FileEncoder encode;

        try {
            encode = new FileEncoder(new java.io.File(filename), Fit.ProtocolVersion.V2_0);
        } catch (FitRuntimeException e) {
            System.err.println("Error opening file " + filename);
            e.printStackTrace();
            return;
        }

        encode.write(fileIdMesg);
        encode.write(deviceInfoMesg);


        List<Mesg> msgs = buildMsg(sessions);
        for (Mesg msg : msgs) {
            encode.write(msg);
        }

        try {
            encode.close();
        } catch (FitRuntimeException e) {
            System.err.println("Error closing encode.");
            e.printStackTrace();
        }
    }

    public List<Mesg> buildMsg(List<FitSession> sessions) {
        List<Mesg> messages = new ArrayList<>();

        //Event
        for (FitSession session : sessions) {
            messages.addAll(session.getEvents());
        }

        //Record
        for (FitSession session : sessions) {
            messages.addAll(session.getRecords());
        }

        //Lap
        for (FitSession session : sessions) {
            messages.addAll(session.getLaps());
        }

        //Session
        SessionMesg sessionMesg = new SessionMesg();
        sessionMesg.setMessageIndex(0);
        sessionMesg.setStartTime(startTime);

        float totalDistance = totalDistance();
        float totalMovingTime = totalMovingTime();
        sessionMesg.setTotalDistance(totalDistance);
        sessionMesg.setTotalElapsedTime((totalElapsedTime()));
        sessionMesg.setTotalMovingTime((totalMovingTime));
        sessionMesg.setTotalTimerTime((totalTimerTime()));
        sessionMesg.setTotalCalories(totalCalories());
        sessionMesg.setTotalAscent(totalAscent());
        sessionMesg.setTotalDescent(totalDescent());
        sessionMesg.setTotalStrokes(totalStrokes());
        sessionMesg.setTotalStrides(totalStrides());
        sessionMesg.setMaxTemperature(maxTemperature());
        sessionMesg.setAvgTemperature(avgTemperature());
        sessionMesg.setAvgHeartRate(avgHeartRate());
        sessionMesg.setMaxHeartRate(maxHeartRate());


        //坡度
        sessionMesg.setAvgGrade(avgGrade());
        sessionMesg.setMaxPosGrade(maxPosGrade());
        sessionMesg.setAvgPosGrade(avgPosGrade());
        sessionMesg.setMaxNegGrade(maxNegGrade());
        sessionMesg.setAvgNegGrade(avgNegGrade());

        //速度
        sessionMesg.setMaxSpeed(maxSpeed());
        sessionMesg.setAvgSpeed(totalDistance / totalMovingTime);
        sessionMesg.setMaxBallSpeed(maxBallSpeed());
        sessionMesg.setAvgBallSpeed(avgBallSpeed());
        sessionMesg.setAvgPosVerticalSpeed(avgPosVerticalSpeed());
        sessionMesg.setMaxPosVerticalSpeed(maxPosVerticalSpeed());
        sessionMesg.setMaxNegVerticalSpeed(maxNegVerticalSpeed());
        sessionMesg.setAvgNegVerticalSpeed(avgNegVerticalSpeed());


        sessionMesg.setMaxAltitude(maxAltitude());
        sessionMesg.setMaxDepth(maxDepth());

        sessionMesg.setSport(sessions.get(0).getSession().getSport());
        sessionMesg.setSubSport(sessions.get(0).getSession().getSubSport());
        sessionMesg.setFirstLapIndex(0);
        sessionMesg.setNumLaps(1);

        messages.add(sessionMesg);


        ActivityMesg activityMesg = sessions.get(0).getActivity();
        activityMesg.setTotalTimerTime(totalTimerTime());
        messages.add(activityMesg);

        return messages;
    }

    private Byte maxTemperature() {
        Byte r = 0;
        for (FitSession session : this.sessions) {
            Byte val = session.getSession().getMaxTemperature();
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    private Byte avgTemperature() {
        float r = 0;
        for (FitSession session : this.sessions) {
            float val = session.getSession().getAvgTemperature();
            r = r + val;
        }
        return (byte) (r / this.sessions.size());
    }

    public short avgHeartRate() {
        float r = 0;
        for (FitSession session : this.sessions) {
            float val = session.getSession().getAvgHeartRate();
            r = r + val;
        }
        return (short) (r / this.sessions.size());
    }

    public float avgPosVerticalSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            float val = session.getSession().getAvgPosVerticalSpeed();
            r = r + val;
        }
        return r / this.sessions.size();
    }

    public float avgNegVerticalSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            float val = session.getSession().getAvgNegVerticalSpeed();
            r = r + val;
        }
        return r / this.sessions.size();
    }


    public float totalDistance() {
        float r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalDistance();
        }
        return r;
    }


    public float totalElapsedTime() {
        return this.endTime.getTimestamp() - this.startTime.getTimestamp();
    }

    public int totalCalories() {
        int r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalCalories();
        }
        return r;
    }

    public int totalAscent() {
        int r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalAscent();
        }
        return r;
    }

    public int totalDescent() {
        int r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalDescent();
        }
        return r;
    }

    public Long totalStrokes() {
        long r = 0;
        for (FitSession session : this.sessions) {
            Long strokes = session.getSession().getTotalStrokes();
            if (strokes == null) {
                return null;
            }
            r = r + strokes;
        }
        return r;
    }

    public Long totalStrides() {
        long r = 0;
        for (FitSession session : this.sessions) {
            Long val = session.getSession().getTotalStrides();
            if (val == null) {
                return null;
            }
            r = r + val;
        }
        return r;
    }

    public float totalMovingTime() {
        float r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalMovingTime();
        }
        return r;
    }

    public float totalTimerTime() {
        float r = 0;
        for (FitSession session : this.sessions) {
            r = r + session.getSession().getTotalTimerTime();
        }
        return r;
    }

    public Float avgGrade() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getAvgGrade();
            if (val == null) {
                return null;
            }
            r = r + val;
        }
        return r / this.sessions.size();
    }

    public Float avgPosGrade() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getAvgPosGrade();
            if (val == null) {
                return null;
            }
            r = r + val;
        }
        return r / this.sessions.size();
    }

    public Float avgNegGrade() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getAvgNegGrade();
            if (val == null) {
                return null;
            }
            r = r + val;
        }
        return r / this.sessions.size();
    }

    public Float avgBallSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getAvgBallSpeed();
            if (val == null) {
                return null;
            }
            r = r + val;
        }
        return r / this.sessions.size();
    }

    public float maxSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            float val = session.getSession().getMaxSpeed();
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxPosGrade() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxPosGrade();
            if (val == null) {
                return null;
            }
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxNegGrade() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxNegGrade();
            if (val == null) {
                return null;
            }
            if (val < r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxAltitude() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxAltitude();
            if (val == null) {
                return null;
            }
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxBallSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxBallSpeed();
            if (val == null) {
                return null;
            }
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxPosVerticalSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxPosVerticalSpeed();
            if (val == null) {
                return null;
            }
            if (val > r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxNegVerticalSpeed() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxNegVerticalSpeed();
            if (val == null) {
                return null;
            }
            if (val < r) {
                r = val;
            }
        }
        return r;
    }

    public Float maxDepth() {
        float r = 0;
        for (FitSession session : this.sessions) {
            Float val = session.getSession().getMaxDepth();
            if (val == null) {
                return null;
            }
            if (val > r) {
                r = val;
            }
        }
        return r;
    }


    public short maxHeartRate() {
        short r = 0;
        for (FitSession session : this.sessions) {
            short val = session.getSession().getMaxHeartRate();
            if (val > r) {
                r = val;
            }
        }
        return r;
    }
}



