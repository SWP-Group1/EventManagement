/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.swp.entity.event;

import fptu.swp.entity.location.LocationDTO;
import fptu.swp.entity.range.RangeDTO;
import fptu.swp.entity.user.LecturerBriefInfoDTO;
import fptu.swp.entity.user.UserDTO;
import fptu.swp.utils.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Statement;
import java.util.Base64;
import javax.naming.NamingException;

/**
 *
 * @author triet
 */
public class EventDAO {

    //Ham lay danh sach event card hs da bam Quan tam
    //Cac comment ap dung cho 3 ham get list EventCard
    public List<EventCardDTO> getFollowedEventList(int studentId) throws SQLException {
        List<EventCardDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        int eventId = 0;
        int currentEventId = 0;
        String eventName = "";
        String eventPoster = "";
        String organizerName = "";
        String date = "";
        String locationName = "";
        int following = 0;
        int joining = 0;
        List<String> listLocation = new ArrayList<>();
        SimpleDateFormat formatter = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql
                        = "SELECT s.id eventId, s.name eventName, s.poster eventPoster, m.name organizerName, t.date date, t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants"
                        + " FROM tblEvents s"
                        + " LEFT JOIN tblUsers m ON s.userId = m.id"
                        + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                        + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                        + " WHERE s.id IN (SELECT eventId FROM tblStudentsInEvents WHERE studentId = ? AND isFollowing = 1)";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, studentId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    eventId = rs.getInt("eventId");
                    if (currentEventId != eventId) { //qua 1 event khac
                        if (currentEventId != 0) { //event khong phai event dau tien
                            int i = 0;
                            String location = "";
                            for (i = 0; i < listLocation.size() - 1; i++) {
                                location += listLocation.get(i) + ", ";
                            }//format cuoi location
                            location += listLocation.get(i);
                            EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                            list.add(card);
                        }
                        listLocation.clear(); //qua moi event khoi tao lai list dia diem
                        currentEventId = eventId;
                        eventName = rs.getString("eventName");
                        byte[] tmp = rs.getBytes("eventPoster");
                        eventPoster = Base64.getEncoder().encodeToString(tmp);
                        organizerName = rs.getString("organizerName");
                        formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                        Date dateFromDB = rs.getTimestamp("date");
                        date = formatter.format(dateFromDB).toString();
                        locationName = rs.getString("locationName"); //lay location cua 1 record
                        following = rs.getInt("followers");
                        joining = rs.getInt("participants");
                        listLocation.add(locationName); // add location vao chuoi
                    } else {
                        locationName = rs.getString("locationName");
                        listLocation.add(locationName);
                    }
                }
                int i = 0;
                String location = "";
                for (i = 0; i < listLocation.size() - 1; i++) {
                    location += listLocation.get(i) + ", ";
                } //format chuoi location
                location += listLocation.get(i);
                EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                list.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public List<EventCardDTO> getJoiningEventList(int studentId) throws SQLException {
        List<EventCardDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        int eventId = 0;
        int currentEventId = 0;
        String eventName = "";
        String eventPoster = "";
        String organizerName = "";
        String date = "";
        String locationName = "";
        int following = 0;
        int joining = 0;
        List<String> listLocation = new ArrayList<>();
        SimpleDateFormat formatter = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql
                        = "SELECT s.id eventId, s.name eventName, s.poster eventPoster, m.name organizerName, t.date date, t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants"
                        + " FROM tblEvents s"
                        + " LEFT JOIN tblUsers m ON s.userId = m.id"
                        + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                        + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                        + " WHERE s.id IN (SELECT eventId FROM tblStudentsInEvents WHERE studentId = ? AND isJoining = 1)";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, studentId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    eventId = rs.getInt("eventId");
                    if (currentEventId != eventId) {
                        if (currentEventId != 0) {
                            int i = 0;
                            String location = "";
                            for (i = 0; i < listLocation.size() - 1; i++) {
                                location += listLocation.get(i) + ", ";
                            }
                            location += listLocation.get(i);
                            EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                            list.add(card);
                        }
                        listLocation.clear();
                        currentEventId = eventId;
                        eventName = rs.getString("eventName");
                        byte[] tmp = rs.getBytes("eventPoster");
                        eventPoster = Base64.getEncoder().encodeToString(tmp);
                        organizerName = rs.getString("organizerName");
                        formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                        Date dateFromDB = rs.getTimestamp("date");
                        date = formatter.format(dateFromDB).toString();
                        locationName = rs.getString("locationName");
                        following = rs.getInt("followers");
                        joining = rs.getInt("participants");
                        listLocation.add(locationName);
                    } else {
                        locationName = rs.getString("locationName");
                        listLocation.add(locationName);
                    }
                }
                int i = 0;
                String location = "";
                for (i = 0; i < listLocation.size() - 1; i++) {
                    location += listLocation.get(i) + ", ";
                }
                location += listLocation.get(i);
                EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                list.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public List<EventCardDTO> getNewFeedEventList(UserDTO loginUser) throws SQLException {
        List<EventCardDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        int eventId = 0;
        int currentEventId = 0;
        String eventName = "";
        String eventPoster = "";
        String organizerName = "";
        String date = "";
        String locationName = "";
        int following = 0;
        int joining = 0;
        List<String> listLocation = new ArrayList<>();
        SimpleDateFormat formatter = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "";
                if ("STUDENT".equals(loginUser.getRoleName())) {
                    sql = "SELECT s.id eventId, s.name eventName, s.poster eventPoster, m.name organizerName, t.date date, t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants"
                            + " FROM tblEvents s"
                            + " LEFT JOIN tblUsers m ON s.userId = m.id"
                            + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                            + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                            + " WHERE s.statusId = 1";
                    stm = conn.prepareStatement(sql);
                } else if ("CLUB'S LEADER".equals(loginUser.getRoleName()) || "DEPARTMENT'S MANAGER".equals(loginUser.getRoleName())) {
                    sql = "SELECT s.id eventId, s.name eventName, s.poster eventPoster, m.name organizerName, t.date date, t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants"
                            + " FROM tblEvents s"
                            + " LEFT JOIN tblUsers m ON s.userId = m.id"
                            + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                            + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                            + " WHERE s.userId = ? AND s.statusId = 1";
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, loginUser.getId());
                } else if ("LECTURER".equals(loginUser.getRoleName())) {
                    sql = "SELECT s.id eventId, s.name eventName, s.poster eventPoster, m.name organizerName, t.date date, t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants"
                            + " FROM tblEvents s"
                            + " LEFT JOIN tblUsers m ON s.userId = m.id"
                            + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                            + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                            + " WHERE s.statusId = 1 AND s.id IN (SELECT eventId FROM tblLecturersInEvents WHERE lecturerId = ? AND statusId = 1)";
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, loginUser.getId());
                }
                rs = stm.executeQuery();
                while (rs.next()) {
                    eventId = rs.getInt("eventId");
                    if (currentEventId != eventId) {
                        if (currentEventId != 0) {
                            int i = 0;
                            String location = "";
                            for (i = 0; i < listLocation.size() - 1; i++) {
                                location += listLocation.get(i) + ", ";
                            }
                            location += listLocation.get(i);
                            EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                            list.add(card);
                        }
                        listLocation.clear();
                        currentEventId = eventId;
                        eventName = rs.getString("eventName");
                        byte[] tmp = rs.getBytes("eventPoster");
                        eventPoster = Base64.getEncoder().encodeToString(tmp);
                        organizerName = rs.getString("organizerName");
                        formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                        Date dateFromDB = rs.getTimestamp("date");
                        date = formatter.format(dateFromDB).toString();
                        locationName = rs.getString("locationName");
                        following = rs.getInt("followers");
                        joining = rs.getInt("participants");
                        listLocation.add(locationName);
                    } else {
                        locationName = rs.getString("locationName");
                        listLocation.add(locationName);
                    }
                }
                int i = 0;
                String location = "";
                for (i = 0; i < listLocation.size() - 1; i++) {
                    location += listLocation.get(i) + ", ";
                }
                location += listLocation.get(i);
                EventCardDTO card = new EventCardDTO(currentEventId, eventName, eventPoster, location, date, organizerName, following, joining);
                list.add(card);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public EventDetailDTO getEventDetail(int eventId) throws SQLException {
        EventDetailDTO detail = null;
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        String eventName = "";
        String eventPoster = "";
        String organizerName = "";
        String date = "";
        String locationName = "";
        int following = 0;
        int joining = 0;
        String description = "";
        String organizerDescription = "";
        String organizerAvatar = "";
        int statusId = 0;
        List<String> listLocation = new ArrayList<>();
        List<RangeDTO> listTime = new ArrayList<>();
        SimpleDateFormat formatter = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql
                        = "SELECT s.name eventName, s.poster eventPoster, m.name organizerName, t.date date,"
                        + " t.name locationName, s.numberOfFollowers followers, s.numberOfParticipants participants,"
                        + " s.description description, m.description organizerDescription, m.avatar organizerAvatar, s.statusId statusId"
                        + " FROM tblEvents s"
                        + " LEFT JOIN tblUsers m ON s.userId = m.id"
                        + " LEFT JOIN ( SELECT DISTINCT eventId, date, u.name FROM tblDateTimeLocation"
                        + "                  LEFT JOIN tblLocations u ON locationId = u.id) t ON s.id = t.eventId"
                        + " WHERE s.id = ?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                rs = stm.executeQuery();
                if (rs.next()) {
                    eventName = rs.getString("eventName");
                    byte[] tmp = rs.getBytes("eventPoster");
                    eventPoster = Base64.getEncoder().encodeToString(tmp);
                    organizerName = rs.getString("organizerName");
                    formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                    Date dateFromDB = rs.getTimestamp("date");
                    date = formatter.format(dateFromDB).toString();
                    locationName = rs.getString("locationName");
                    following = rs.getInt("followers");
                    joining = rs.getInt("participants");
                    description = rs.getString("description");
                    organizerDescription = rs.getString("organizerDescription");
                    organizerAvatar = rs.getString("organizerAvatar");
                    statusId = rs.getInt("statusId");
                    listLocation.add(locationName);
                }
                while (rs.next()) {
                    locationName = rs.getString("locationName");
                    listLocation.add(locationName);
                }
                int i = 0;
                //format location string
                String location = "";
                for (i = 0; i < listLocation.size() - 1; i++) {
                    location += listLocation.get(i) + ", ";
                }
                location += listLocation.get(i);
                sql = "SELECT v.rangeId rangeId, u.rangeName rangeName, u.detail detail"
                        + " FROM tblTimeRanges u"
                        + " RIGHT JOIN (SELECT DISTINCT v.rangeId"
                        + "                   FROM tblEvents u, tblDateTimeLocation v"
                        + "                   WHERE u.id = v.eventId AND v.eventId = ?) v"
                        + " ON u.id = v.rangeId";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                rs = stm.executeQuery();

                while (rs.next()) {
                    int rangeId = rs.getInt("rangeId");
                    String rangeName = rs.getString("rangeName");
                    String rangeDetail = rs.getString("detail");
                    RangeDTO range = new RangeDTO(rangeId, rangeName, rangeDetail);
                    listTime.add(range);
                }
                //format time string (gan name cua cac time range)
                RangeDTO startSlot = new RangeDTO(Integer.MAX_VALUE);
                RangeDTO endSlot = new RangeDTO(Integer.MIN_VALUE);
                for (RangeDTO range : listTime) {
                    int slotId = range.getId();
                    if (slotId < startSlot.getId()) {
                        startSlot = range;
                    }
                    if (slotId > endSlot.getId()) {
                        endSlot = range;
                    }
                }
                String time = "Slot " + startSlot.getId() + " - " + endSlot.getId()
                        + " (" + startSlot.getDetail().substring(0, 5) + " - " + endSlot.getDetail().substring(7) + ")";
                detail = new EventDetailDTO(eventId, eventName, eventPoster, location, date, time, organizerName, following, joining, description, organizerDescription, organizerAvatar, statusId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return detail;
    }

    public List<CommentDTO> getListCommentByEventId(int eventId, boolean isQuestion) throws SQLException {
        List<CommentDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        PreparedStatement stm2 = null;
        ResultSet rs2 = null;
        int commentId = 0;
        String contents = "";
        String userAvatar = "";
        String userName = "";
        Date commentDatetime;
        List<ReplyDTO> replyList = new ArrayList<>();

        SimpleDateFormat formatter = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "SELECT u.commentId commentId, u.contents contents, v.avatar userAvatar,"
                        + " v.name userName, u.isQuestion isQuestion, u.commentDatetime commentDatetime"
                        + " FROM tblComments u"
                        + " LEFT JOIN tblUsers v ON u.userId = v.id"
                        + " WHERE u.eventId = ? AND u.replyId IS NULL AND u.isQuestion = ?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                stm.setBoolean(2, isQuestion);
                rs = stm.executeQuery();
                while (rs.next()) {
                    commentId = rs.getInt("commentId");
                    contents = rs.getString("contents");
                    userAvatar = rs.getString("userAvatar");
                    userName = rs.getString("userName");
                    isQuestion = rs.getBoolean("isQuestion");
                    commentDatetime = rs.getTimestamp("commentDatetime");
                    String sql2 = "SELECT u.commentId commentId, u.contents contents, v.avatar userAvatar, v.name userName, u.commentDatetime replyDatetime"
                            + " FROM tblComments u"
                            + " LEFT JOIN tblUsers v ON u.userId = v.id"
                            + " WHERE eventId = ? AND replyId = ? AND isQuestion = 0";
                    stm2 = conn.prepareStatement(sql2);
                    stm2.setInt(1, eventId);
                    stm2.setInt(2, commentId);
                    rs2 = stm2.executeQuery();
                    while (rs2.next()) {
                        int replyCommentId = rs2.getInt("commentId");
                        String replyContents = rs2.getString("contents");
                        String replyUserAvatar = rs2.getString("userAvatar");
                        String replyUserName = rs2.getString("userName");
                        Date replyCommentDatetime = rs2.getTimestamp("replyDatetime");
                        replyList.add(new ReplyDTO(replyCommentId, replyContents, replyUserAvatar, replyUserName, replyCommentDatetime));
                    }
                    if (replyList.size() > 0) {
                        Collections.sort(replyList);
                    }
                    CommentDTO tmp = new CommentDTO(commentId, contents, eventId, userAvatar, userName, isQuestion, commentDatetime, replyList);
                    list.add(tmp);
                    System.out.println();
                    replyList = new ArrayList<>();
                }
                Collections.sort(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs2 != null) {
                rs2.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stm2 != null) {
                stm2.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public int insertNewEvent(EventDetailDTO detail, int organizerId, FileInputStream savedPic) throws SQLException {
        int eventId = 0;
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet generatedKeys = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "INSERT INTO tblEvents(name, description, poster, createDate, statusId, userId, numberOfFollowers, numberOfParticipants) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,1,?,0,0)";
                stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, detail.getName());
                stm.setString(2, detail.getDescription());
                stm.setBinaryStream(3, savedPic);
                stm.setInt(4, organizerId);
                check = stm.executeUpdate() > 0;
                if (check) {
                    generatedKeys = stm.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        eventId = generatedKeys.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (generatedKeys != null) {
                generatedKeys.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return eventId;
    }

    public boolean insertNewEventDateTimeLocation(String chosenDate, List<LocationDTO> chosenLocationList, String chosenTimeRange, int eventId) throws SQLException {
        boolean check = false;
        int startSlot = Integer.parseInt(String.valueOf(chosenTimeRange.charAt(0)));
        int endSlot = Integer.parseInt(String.valueOf(chosenTimeRange.charAt(4)));

        Connection conn = null;
        PreparedStatement stm = null;
        try {
            Date date = new SimpleDateFormat("EEEE, dd/MM/yyyy").parse(chosenDate);
            conn = DBHelper.makeConnection();
            if (conn != null) {
                for (LocationDTO location : chosenLocationList) {
                    for (int i = startSlot; i <= endSlot; i++) {
                        String sql = "INSERT INTO tblDateTimeLocation(eventId, rangeId, locationId, date, statusId) "
                                + " VALUES(?,?,?,?,1)";
                        stm = conn.prepareStatement(sql);
                        stm.setInt(1, eventId);
                        stm.setInt(2, i);
                        stm.setInt(3, location.getId());
                        stm.setDate(4, new java.sql.Date(date.getTime()));
                        check = stm.executeUpdate() > 0;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean insertNewEventLecturer(List<LecturerBriefInfoDTO> chosenLecturerList, int eventId) throws SQLException {
        if (chosenLecturerList.size() == 0) {
            return true;
        }
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                for (LecturerBriefInfoDTO lecturer : chosenLecturerList) {
                    String sql = "INSERT INTO tblLecturersInEvents(eventId, lecturerId, statusId) "
                            + " VALUES(?,?,3)";
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, eventId);
                    stm.setInt(2, lecturer.getId());
                    check = stm.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean checkFollowed(int studentId, int eventId) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "SELECT eventId, studentId, isFollowing"
                        + " FROM tblStudentsInEvents"
                        + " WHERE eventId = ? AND studentId = ?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                stm.setInt(2, studentId);
                rs = stm.executeQuery();
                if (rs.next()) {
                    check = rs.getBoolean("isFollowing");
                } else {
                    check = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
            return check;
        }
    }

    public boolean checkJoining(int studentId, int eventId) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "SELECT eventId, studentId, isJoining"
                        + " FROM tblStudentsInEvents"
                        + " WHERE eventId = ? AND studentId = ?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                stm.setInt(2, studentId);
                rs = stm.executeQuery();
                if (rs.next()) {
                    check = rs.getBoolean("isJoining");
                } else {
                    check = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
            return check;
        }
    }

    //ham check ton tai xem da co thong tin cua student va event nay trong tblStudentsInEvents hay chua
    //neu da co -> return true;
    //neu chua co -> insert them (set isJoining and isFollowing = false) -> return true;
    //con lai return false;
    public boolean checkExistenceAndOrInsertStudentInEvent(int eventId, int studentId) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "SELECT eventId, studentId" //check if this student is already in tblStudentsInEvents
                        + " FROM tblStudentsInEvents"
                        + " WHERE eventId = ? AND studentId = ?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, eventId);
                stm.setInt(2, studentId);
                rs = stm.executeQuery();
                if (!rs.next()) {
                    sql = "INSERT INTO tblStudentsInEvents(eventId, studentId, isFollowing, isJoining) "
                            + " VALUES(?,?,0,0)";
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, eventId);
                    stm.setInt(2, studentId);
                    check = stm.executeUpdate() > 0;
                } else {
                    check = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
            return check;
        }
    }

    public boolean setFollowingStatus(int eventId, int studentId, boolean isFollowing) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "UPDATE tblStudentsInEvents"
                        + " SET isFollowing = ?"
                        + " WHERE eventId = ? AND studentId = ?";
                stm = conn.prepareStatement(sql);
                stm.setBoolean(1, isFollowing);
                stm.setInt(2, eventId);
                stm.setInt(3, studentId);
                if (stm.executeUpdate() > 0) {
                    if (isFollowing) {
                        sql = "UPDATE tblEvents"
                                + " SET numberOfFollowers = numberOfFollowers + 1"
                                + " WHERE id = ?";
                    } else {
                        sql = "UPDATE tblEvents"
                                + " SET numberOfFollowers = numberOfFollowers - 1"
                                + " WHERE id = ?";
                    }
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, eventId);
                    check = stm.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
            return check;
        }
    }

    public boolean setJoiningStatus(int eventId, int studentId, boolean isJoining) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {
                String sql = "UPDATE tblStudentsInEvents"
                        + " SET isJoining = ?"
                        + " WHERE eventId = ? AND studentId = ?";
                stm = conn.prepareStatement(sql);
                stm.setBoolean(1, isJoining);
                stm.setInt(2, eventId);
                stm.setInt(3, studentId);
                if (stm.executeUpdate() > 0) {
                    if (isJoining) {
                        sql = "UPDATE tblEvents"
                                + " SET numberOfParticipants = numberOfParticipants + 1"
                                + " WHERE id = ?";
                    } else {
                        sql = "UPDATE tblEvents"
                                + " SET numberOfParticipants = numberOfParticipants - 1"
                                + " WHERE id = ?";
                    }
                    stm = conn.prepareStatement(sql);
                    stm.setInt(1, eventId);
                    check = stm.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
            return check;
        }
    }

    public boolean insertComment(int eventId, int userId, String contents) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = DBHelper.makeConnection();
            if (conn != null) {

                String sql = "INSERT INTO tblComments(contents, replyId, eventId, userId, isQuestion, commentDatetime)"
                        + " VALUES(?, NULL, ?, ?, 0, CURRENT_TIMESTAMP)";
                stm = conn.prepareStatement(sql);
                stm.setString(1,contents);
                stm.setInt(2, eventId);
                stm.setInt(3, userId);
                check = stm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
}