package seebee.geebeeview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.account.PasswordEncrypter;
import seebee.geebeeview.model.account.User;
import seebee.geebeeview.model.consultation.ChiefComplaint;
import seebee.geebeeview.model.consultation.HPI;
import seebee.geebeeview.model.consultation.Impressions;
import seebee.geebeeview.model.consultation.Municipality;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.PatientAnswers;
import seebee.geebeeview.model.consultation.PositiveResults;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.consultation.Symptom;
import seebee.geebeeview.model.consultation.SymptomFamily;
import seebee.geebeeview.model.monitoring.IdealValue;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;

/**
 * The DatabaseAdapter class contains methods which serve as database queries.
 * The original class used as a basis for this current version was
 * created by Mike Dayupay for HPI Generation module of the GetBetter project.
 *
 * @author Mike Dayupay
 * @author Mary Grace Malana
 * @author Stephanie Dy
 */

public class DatabaseAdapter {

    /**
     * Used to identify the source of a log message
     */
    protected static final String TAG = "DatabaseAdapter";
    /**
     * use as the database of the app
     */
    private SQLiteDatabase getBetterDb;
    /**
     * Contains the helper class of the database
     */
    private DatabaseHelper getBetterDatabaseHelper;

    /**
     * Creates a new instance of {@link DatabaseHelper}.
     * @param context current context.
     */
    public DatabaseAdapter(Context context) {
        getBetterDatabaseHelper  = new DatabaseHelper(context);
    }

    /**
     * Creates the database using the helper class
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     */
    public DatabaseAdapter createDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    /**
     * Opens the database for read or write unless the database problem occurs
     * that limits the user from writing to the database.
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     *
     * @see #openDatabaseForRead()
     */
    public DatabaseAdapter openDatabaseForRead() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getReadableDatabase();

        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    /**
     * Opens the database for read or write. Method call may fail
     * if a database problem occurs.
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     *
     * @see #openDatabaseForRead()
     */
    public DatabaseAdapter openDatabaseForWrite() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getWritableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    /**
     * Closes the database
     */
    public void closeDatabase() {
        getBetterDatabaseHelper.close();
    }

    /**
     * Sets all symptom's {@code is_answered} column to 0.
     * 0 means that the symptom hasn't been answered yet.
     */
    public void resetSymptomAnsweredFlag () {

        ContentValues values = new ContentValues();
        values.put("is_answered", 0);
        int count = getBetterDb.update(Symptom.TABLE_NAME, values, null, null);

        //Log.d("updated rows reset", count + "");
    }

    /**
     * Sets all symptom family's {@code answered_flag} to 0
     * and {@code answer_status} to 1.
     */
    public void resetSymptomFamilyFlags() {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 0);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SymptomFamily.TABLE_NAME, values, null, null);

        //Log.d("updated rows reset", count +"");
    }

    /**
     * Gets the list of impressions that were filtered using the chief complaints.
     * @param chiefComplaints list of complaints used for filtering the impressions.
     * @return list of impression that is related to each of the chief complaints specified.
     */
    public ArrayList<Impressions> getImpressions (ArrayList<ChiefComplaint> chiefComplaints) {

        ArrayList<Impressions> results = new ArrayList<Impressions>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tbl_case_impression AS i, tbl_impressions_of_complaints AS s " +
                "WHERE i._id = s.impression_id AND (");

        for(ChiefComplaint c: chiefComplaints){
            sql.append("s.complaint_id = ").append(c.getComplaintID()).append(" OR ");
        }

        sql.delete(sql.lastIndexOf(" OR "), sql.length());

        sql.append(")");
        Log.d(TAG, "SQL Statement: " + sql);

        Cursor c = getBetterDb.rawQuery(sql.toString(), null);

        while(c.moveToNext()) {
            Impressions impressions = new Impressions(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("medical_term")),
                    c.getString(c.getColumnIndexOrThrow("scientific_name")),
                    c.getString(c.getColumnIndexOrThrow("local_name")),
                    c.getString(c.getColumnIndexOrThrow("treatment_protocol")),
                    c.getString(c.getColumnIndexOrThrow("remarks")));

            results.add(impressions);
        }

        c.close();
        return results;
    }

    /**
     * Gets the symptoms that are related to the specified impression.
     * @param impressionId ID of the impression.
     * @return list of symptoms that is associated to the corresponding
     * impression which has an ID value of {@code impressionId}.
     */
    public ArrayList<Symptom> getSymptoms(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<Symptom>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id";

        Cursor c = getBetterDb.rawQuery(sql, null);
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")),
                    c.getInt(c.getColumnIndexOrThrow("emotion")));

            results.add(symptom);
        }

        c.close();
        return results;
    }

    /**
     * Updates the answered_flag and answer_status row to 1 of the chief complaint
     * which has an ID value of {@code chiefComplaintId}.
     * @param chiefComplaintId ID of the chief complaint to be updated.
     */
    public void updateAnsweredStatusSymptomFamily(int chiefComplaintId) {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 1);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SymptomFamily.TABLE_NAME, values, "related_chief_complaint_id = " + chiefComplaintId, null);

        //Log.d("updated rows symptom family flags", count + "");
    }

    /**
     * Like {@link #getSymptoms(int)} this method gets the symptoms
     * that are related to the specified impression. However only the symptoms that
     * haven't been answered will be returned.
     *
     * @param impressionId ID of the impression.
     * @return list of unanswered symptoms that is associated to the corresponding
     * impression which has an ID value of {@code impressionId}
     */
    public ArrayList<Symptom> getQuestions(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<Symptom>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id AND s.is_answered = 0";

        Cursor c = getBetterDb.rawQuery(sql, null);
        Log.d("query count", c.getCount() + "");
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")),
                    c.getInt(c.getColumnIndexOrThrow("emotion")));

            results.add(symptom);
        }

        c.close();
        return results;
    }

    /**
     * Returns whether the symptom family with the specified ID is answered.
     * @param symptomFamilyId ID of the symptom family to be queried.
     * @return true if the symptom family is answered, else false.
     */
    public boolean symptomFamilyIsAnswered (int symptomFamilyId) {

        Log.d("id", symptomFamilyId + "");
        String sql = "SELECT answered_flag FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();


        if(c.getCount() == 0) {
            c.close();
            return true;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answered_flag")) == 1) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
    }

    /**
     * Gets the symptom family which has an ID value of {@code symptomFamilyId}
     * @param symptomFamilyId ID of the symptom family to be searched
     * @return symptom family with the corresponding ID
     */
    public SymptomFamily getGeneralQuestion (int symptomFamilyId) {

        String sql = "SELECT * FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        SymptomFamily generalQuestion;
        generalQuestion = new SymptomFamily(c.getInt(c.getColumnIndexOrThrow("_id")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_english")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_tagalog")),
                c.getString(c.getColumnIndexOrThrow("general_question_english")),
                c.getString(c.getColumnIndexOrThrow("responses_english")),
                c.getInt(c.getColumnIndexOrThrow("related_chief_complaint_id")));

        c.close();
        return generalQuestion;
    }


    /**
     * Updates the is_answered row to 1 of the symptom
     * which has an ID value of {@code symptomId}.
     * @param symptomId ID of the symptom to be updated.
     */
    public void updateAnsweredFlagPositive(int symptomId) {

        ContentValues values = new ContentValues();
        values.put("is_answered", 1);
        int count = getBetterDb.update(Symptom.TABLE_NAME, values, "_id = " + symptomId, null);

        //Log.d("update rows flag positive", count + "");
    }

    /**
     * Updates the answer_flag row to 1 of the symptom family
     * which has an ID value of {@code symptomId}. Also
     * updates the value of answer_status row of the symptom family
     * to the value of {@code answer}.
     * @param symptomFamilyId ID of the symptom to be updated.
     * @param answer of the user to the symptom family question.
     */
    public void updateAnsweredStatusSymptomFamily(int symptomFamilyId, int answer) {

        String sql = "UPDATE tbl_symptom_family SET answered_flag = 1, answer_status = " + answer +
                " WHERE _id = " + symptomFamilyId;

        getBetterDb.execSQL(sql);
    }

    /**
     * Gets the symptoms that are associated as hard symptoms to the
     * which has an ID value of {@code impressionId}
     * @param impressionId ID of the impression to be queried.
     * @return hard symptoms of the specified impression
     */
    public ArrayList<String> getHardSymptoms (int impressionId) {

        ArrayList<String> results = new ArrayList<String>();

        String sql = "SELECT s.symptom_name_english AS symptom_name_english FROM tbl_symptom_list AS s, " +
                "tbl_symptom_of_impression AS i WHERE i.impression_id = " + impressionId +
                " AND i.hard_symptom = 1 AND i.symptom_id = s._id";
        Cursor c = getBetterDb.rawQuery(sql, null);


        while(c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("symptom_name_english")));
        }

        c.close();
        return results;
    }

    /**
     * Returns whether the symptom family with the specified ID row answer_status is equal to 1.
     * @param symptomFamilyId ID of the symptom family to be queried.
     * @return true if the symptom family row answer_status is equal to 1, else false.
     */
    public boolean symptomFamilyAnswerStatus (int symptomFamilyId) {

        Log.d("symptom family id", symptomFamilyId + "");
        String sql = "SELECT answer_status FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        if(c.getCount() == 0) {
            c.close();
            return false;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answer_status")) == 1) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
    }

    /**
     * Gets the English phrase of the chief complaint which
     * has an ID value of {@code chiefComplaintIds}.
     * @param chiefComplaintIds ID of the chief complaint to be queried.
     * @return english phrase of the chief complaint.
     */
    public String getChiefComplaints(int chiefComplaintIds) {

        String result = "";
        String sql = "SELECT chief_complaint_english FROM tbl_chief_complaint WHERE _id = " + chiefComplaintIds;
        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getString(c.getColumnIndexOrThrow("chief_complaint_english"));

        //Log.d("result", result);
        c.close();
        return result;
    }

    /**
     * Gets the symptoms that the user answered positively (Yes) to.
     * The symptoms are returned as PositiveResults objects.
     * @param patientAnswers list of patient answers.
     * @return list of positive results.
     */
    public ArrayList<PositiveResults> getPositiveSymptoms (ArrayList<PatientAnswers> patientAnswers) {
        ArrayList<PositiveResults> results = new ArrayList<PositiveResults>();
        String delim = "";

        StringBuilder sql = new StringBuilder("Select symptom_name_english AS positiveSymptom, answer_phrase AS answerPhrase" +
                " FROM tbl_symptom_list WHERE ");

        for(PatientAnswers answer: patientAnswers){
            if(answer.getAnswer().equals("Yes")){
                sql.append(delim).append("_id = ").append(answer.getSymptomId());
                delim = " OR ";
            }
        }

        Log.d(TAG, "SQL Statement: " + sql);
        Cursor c = getBetterDb.rawQuery(sql.toString(), null);

        while(c.moveToNext()) {
            PositiveResults positive = new PositiveResults(c.getString(c.getColumnIndexOrThrow("positiveSymptom")),
                    c.getString(c.getColumnIndexOrThrow("answerPhrase")));

            results.add(positive);
        }

        c.close();
        return results;
    }

    /*************************
     * The succeeding code are not part of the original code created by Mike Dayupay
     * but were created by Mary Grace Malana
     */
    /* GeeBee View does not include the addition of data thus insert methods will be removed
    /**
     * Insert {@code patient} to the database.
     * @param patient Patient to be added to the database.

    public void insertPatient(Patient patient){
        ContentValues values = new ContentValues();
        int row;

        values.put(Patient.C_FIRST_NAME, patient.getFirstName());
        values.put(Patient.C_LAST_NAME, patient.getLastName());
        values.put(Patient.C_BIRTHDAY, patient.getBirthday());
        values.put(Patient.C_GENDER, patient.getGender());
        values.put(Patient.C_SCHOOL_ID, patient.getSchoolId());
        values.put(Patient.C_HANDEDNESS, patient.getHandedness());
        values.put(Patient.C_REMARKS_STRING, patient.getRemarksString());
        values.put(Patient.C_REMARKS_AUDIO, patient.getRemarksAudio());

        row = (int) getBetterDb.insert(Patient.TABLE_NAME, null, values);
        Log.d(TAG, "insertPatient Result: " + row);
    }
    */
    /**
     * Insert {@code record} to the database.
     * @param record Record to be inserted to the database.*/

    public void insertRecord(Record record){
        ContentValues values = new ContentValues();
        int row;

        values.put(Record.C_PATIENT_ID, record.getPatient_id());
        values.put(Record.C_DATE_CREATED, record.getDateCreated());
        values.put(Record.C_HEIGHT, record.getHeight());
        values.put(Record.C_WEIGHT, record.getWeight());
        values.put(Record.C_VISUAL_ACUITY_LEFT, record.getVisualAcuityLeft());
        values.put(Record.C_VISUAL_ACUITY_RIGHT, record.getVisualAcuityRight());
        values.put(Record.C_COLOR_VISION, record.getColorVision());
        values.put(Record.C_HEARING_LEFT, record.getHearingLeft());
        values.put(Record.C_HEARING_RIGHT, record.getHearingRight());
        values.put(Record.C_GROSS_MOTOR, record.getGrossMotor());
        values.put(Record.C_FINE_MOTOR_DOMINANT, record.getFineMotorDominant());
        values.put(Record.C_FINE_MOTOR_N_DOMINANT, record.getFineMotorNDominant());
        values.put(Record.C_FINE_MOTOR_HOLD, record.getFineMotorHold());
        values.put(Record.C_VACCINATION, record.getVaccination());
        values.put(Record.C_PATIENT_PICTURE, record.getPatientPicture());
        values.put(Record.C_REMARKS_STRING, record.getRemarksString());
        values.put(Record.C_REMARKS_AUDIO, record.getRemarksAudio());

        row = (int) getBetterDb.insert(Record.TABLE_NAME, null, values);
        Log.d(TAG, "insertRecord Result: " + row);
    }

    /**
     * Insert {@code hpi} to the database.
     * @param hpi HPI to be inserted to the database.

    public void insertHPI(HPI hpi){
        ContentValues values = new ContentValues();
        int row;

        values.put(HPI.C_PATIENT_ID, hpi.getPatientID());
        values.put(HPI.C_DATE_CREATED, hpi.getDateCreated());
        values.put(HPI.C_HPI, hpi.getHpiText());

        row = (int) getBetterDb.insert(HPI.TABLE_NAME, null, values);
        Log.d(TAG, "insertHPI Result: " + row);
    }
     */
    /**
     * Get all the schools in the database
     * @return list of school retrieved from the database.
     */
    public ArrayList<School> getAllSchools(){
        ArrayList<School> schools = new ArrayList<School>();
        Cursor c = getBetterDb.rawQuery("SELECT "+ School.C_SCHOOL_ID + ", s." + School.C_SCHOOLNAME + ", m." +
                Municipality.C_NAME + " AS municipalityName FROM " + School.TABLE_NAME + " AS s, " + Municipality.TABLE_NAME +
                " AS m WHERE s." + School.C_MUNICIPALITY + " = m." + Municipality.C_MUNICIPALITY_ID, null);
        if(c.moveToFirst()){
            do{
                schools.add(new School(c.getInt(c.getColumnIndex(School.C_SCHOOL_ID)), c.getString(c.getColumnIndex(School.C_SCHOOLNAME)),
                        c.getString(c.getColumnIndex("municipalityName"))));
            }while(c.moveToNext());
        }
        c.close();
        return schools;
    }

    /**
     * Get all records of the patient which has an ID value of
     * {@code patientID}.
     * @param patientId ID of the patient to be queried.
     * @return list of records of the patient.
     */
    public ArrayList<Record> getRecords(int patientId){
        ArrayList<Record> records = new ArrayList<Record>();
        Cursor c = getBetterDb.query(Record.TABLE_NAME, null, Record.C_PATIENT_ID + " = " + patientId, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                Record record = new Record(c.getInt(c.getColumnIndex(Record.C_RECORD_ID)), c.getInt(c.getColumnIndex(Record.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Record.C_DATE_CREATED)), /*c.getString(c.getColumnIndex(Record.C_GRADE_LEVEL)),*/
                        c.getDouble(c.getColumnIndex(Record.C_HEIGHT)),
                        c.getDouble(c.getColumnIndex(Record.C_WEIGHT)), c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_LEFT)),
                        c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_RIGHT)), c.getString(c.getColumnIndex(Record.C_COLOR_VISION)),
                        c.getString(c.getColumnIndex(Record.C_HEARING_LEFT)), c.getString(c.getColumnIndex(Record.C_HEARING_RIGHT)),
                        c.getInt(c.getColumnIndex(Record.C_GROSS_MOTOR)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_N_DOMINANT)),
                        c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_DOMINANT)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_HOLD)),
                        c.getBlob(c.getColumnIndex(Record.C_VACCINATION)), c.getBlob(c.getColumnIndex(Record.C_PATIENT_PICTURE)),
                        c.getString(c.getColumnIndex(Record.C_REMARKS_STRING)), c.getBlob(c.getColumnIndex(Record.C_REMARKS_AUDIO)));

                record.printRecord();
                records.add(record);

            }while(c.moveToNext());
        }
        c.close();
        return records;
    }

    /**
     * Get all HPI of the patient which has an ID value of
     * {@code patientID}.
     * @param patientId ID of the patient to be queried.
     * @return list of HPI of the patient.
     */
    public ArrayList<HPI> getHPIs(int patientId){
        ArrayList<HPI> HPIs = new ArrayList<HPI>();
        Cursor c = getBetterDb.query(HPI.TABLE_NAME, null, HPI.C_PATIENT_ID + " = " + patientId, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                HPIs.add(new HPI(c.getInt(c.getColumnIndex(HPI.C_HPI_ID)), c.getInt(c.getColumnIndex(HPI.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(HPI.C_HPI_TEXT)), c.getString(c.getColumnIndex(HPI.C_DATE_CREATED))));
            }while(c.moveToNext());
        }
        c.close();
        return HPIs;
    }

    /**
     * Get all patients of the school which has an ID value of
     * {@code schoolID}.
     * @param schoolID ID of the school to be queried.
     * @return list of records of the patient.
     */

    public ArrayList<Patient> getPatientsFromSchool(int schoolID){
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor c = getBetterDb.query(Patient.TABLE_NAME, null, Patient.C_SCHOOL_ID + " = " + schoolID, null, null, null, Patient.C_LAST_NAME +" ASC");
        if(c.moveToFirst()){
            do{
                patients.add(new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                        c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                        c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO))));
            }while(c.moveToNext());
        }
        c.close();

        return patients;
    }

    /********************************************
     * The succeeding code are not part of the original code created by Mike Dayupay or Mary Grace Malana
     * but by Stephanie Dy
    */

    /**
     * Insert {@code user} to the database.
     * @param user User to be inserted to the database.
     */
    public int insertUser(User user) throws SQLiteConstraintException {
        ContentValues values = new ContentValues();
        int row = -1;
        /* Encrypt password before storing in database */
        PasswordEncrypter encrypter = new PasswordEncrypter();
        byte[] encodedPassword = encrypter.encryptPassword(user.getPassword());
        encrypter.decodePassword(encodedPassword);

        values.put(User.C_USERNAME, user.getUsername());
        values.put(User.C_PASSWORD, encodedPassword);

        try {
            row = (int) getBetterDb.insertOrThrow(User.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "insertUser Result: " + row);
            e.printStackTrace();
            throw e;
        }

        return row;
    }
    /**
     * Get the user which has a value of
     * {@code username}.
     * @param username of the user requesting access
     * @return User with password
     */
     public User getUser(String username) {
         User user = null;
         try {
             openDatabaseForRead();
         } catch (SQLException e) {
             e.printStackTrace();
         }
         Cursor c = getBetterDb.query(User.TABLE_NAME, null,
                 User.C_USERNAME + " = '" + username + "'", null, null, null, null, null);

         if(c.moveToFirst()){
             /* Decrypt user password first after retrieval */
             byte[] encodedPassword = c.getBlob(c.getColumnIndex(User.C_PASSWORD));
             PasswordEncrypter decrypter = new PasswordEncrypter();
             String decodedPassword = decrypter.decodePassword(encodedPassword);

             user = new User(c.getInt(c.getColumnIndex(User.C_USER_ID)),
                     c.getString(c.getColumnIndex(User.C_USERNAME)), decodedPassword,
                     c.getInt(c.getColumnIndex(User.C_ACCESS)));
         }
         c.close();
         return user;
     }
    /**
     * Get all datasets from records.
     * @return list of schools and date of records.
     */
    public ArrayList<Dataset> getAllDatasets() {
        ArrayList<Dataset> datasetList = new ArrayList<>();
        try {
            openDatabaseForRead();
        }catch (SQLException e){
            Log.e(TAG," ERROR OPEN DB");
            e.printStackTrace();
        }
        Cursor c = getBetterDb.rawQuery("SELECT * FROM tbl_dataset;", null);

        if(c.moveToFirst()) {
            Log.d(TAG, "tbl_dataset is not empty");
            do {
                Dataset dataset = new Dataset(c.getInt(c.getColumnIndex(Dataset.C_ID)),
                        c.getInt(c.getColumnIndex(Dataset.C_SCHOOL_ID)),
                        getSchoolName(c.getInt(c.getColumnIndex(Dataset.C_SCHOOL_ID))),
                        c.getString(c.getColumnIndex(Dataset.C_DATE_CREATED)),
                        c.getInt(c.getColumnIndex(Dataset.C_STATUS)));
                dataset.printDataset();
                datasetList.add(dataset);
            } while (c.moveToNext());
        } else {
            Log.d(TAG, "tbl_dataset is empty!");
        }
        c.close();
        return datasetList;
    }

    /**
     * Get records from schools with unique value of
     * {@code schoolID} and {@code date}.
     * @param schoolID, @param date
     * @return list of schools and date of records.
     */
    public ArrayList<PatientRecord> getRecordsFromSchool(int schoolID, String date) {
        ArrayList<PatientRecord> records = new ArrayList<PatientRecord>();
        int gender;
        String birthday;
        Cursor c = getBetterDb.rawQuery("SELECT * "
                +" FROM "+School.TABLE_NAME+" AS s, "+Record.TABLE_NAME+" AS r, "+Patient.TABLE_NAME+" AS p "
                +" WHERE s."+School.C_SCHOOL_ID+" = p."+Patient.C_SCHOOL_ID
                +" AND r."+Record.C_PATIENT_ID+" = p."+Patient.C_PATIENT_ID
                +" AND s."+School.C_SCHOOL_ID+" = "+schoolID
                +" AND "+Record.C_DATE_CREATED+" LIKE '"+date+"'", null);

        if(c.moveToFirst()){
            do{
                Record record = new Record(c.getInt(c.getColumnIndex(Record.C_RECORD_ID)), c.getInt(c.getColumnIndex(Record.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Record.C_DATE_CREATED)), /*c.getString(c.getColumnIndex(Record.C_GRADE_LEVEL)),*/
                        c.getDouble(c.getColumnIndex(Record.C_HEIGHT)),
                        c.getDouble(c.getColumnIndex(Record.C_WEIGHT)), c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_LEFT)),
                        c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_RIGHT)), c.getString(c.getColumnIndex(Record.C_COLOR_VISION)),
                        c.getString(c.getColumnIndex(Record.C_HEARING_LEFT)), c.getString(c.getColumnIndex(Record.C_HEARING_RIGHT)),
                        c.getInt(c.getColumnIndex(Record.C_GROSS_MOTOR)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_N_DOMINANT)),
                        c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_DOMINANT)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_HOLD)),
                        c.getBlob(c.getColumnIndex(Record.C_VACCINATION)), c.getBlob(c.getColumnIndex(Record.C_PATIENT_PICTURE)),
                        c.getString(c.getColumnIndex(Record.C_REMARKS_STRING)), c.getBlob(c.getColumnIndex(Record.C_REMARKS_AUDIO)));
                gender = c.getInt(c.getColumnIndex(Patient.C_GENDER));
                birthday = c.getString(c.getColumnIndex(Patient.C_BIRTHDAY));
                PatientRecord patientRecord = new PatientRecord(gender, birthday, record);
                //record.printRecord();
                //Log.v(TAG, "Gender: "+gender);
                //Log.v(TAG, "Birthday: "+birthday);
                records.add(patientRecord);

            }while(c.moveToNext());
        }
        c.close();
        return records;
    }

    /**
     * Get the patient which has an ID value of
     * {@code patientID}.
     * @param patientID ID of the patient to be queried.
     * @return the record of the patient.
     */

    public Patient getPatient(int patientID){
        Patient patient = null;
        Cursor c = getBetterDb.query(Patient.TABLE_NAME, null, Patient.C_PATIENT_ID + " = " + patientID, null, null, null, null);
        if(c.moveToFirst()){
            do{
                patient = new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                        c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                        c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO)));
            }while(c.moveToNext());
        }
        c.close();

        return patient;
    }

    /**
     * Get all HPI of the school which has an ID value of
     * {@code schoolID}.
     * @param schoolId ID of the patient to be queried.
     * @return list of HPI of the patient.
     */
    public ArrayList<HPI> getHPIsFromSchool(int schoolId){
        ArrayList<HPI> HPIs = new ArrayList<HPI>();
        Cursor c = getBetterDb.rawQuery("SELECT * "
                +" FROM "+HPI.TABLE_NAME+" AS h, "+Patient.TABLE_NAME+" AS p, "+School.TABLE_NAME+" AS s "
                +" WHERE h."+HPI.C_PATIENT_ID+" = p."+Patient.C_PATIENT_ID
                +" AND p."+Patient.C_SCHOOL_ID+" = s."+School.C_SCHOOL_ID
                +" AND s."+School.C_SCHOOL_ID+" = "+schoolId, null);

        if(c.moveToFirst()){
            do{
                HPIs.add(new HPI(c.getInt(c.getColumnIndex(HPI.C_HPI_ID)), c.getInt(c.getColumnIndex(HPI.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(HPI.C_HPI_TEXT)), c.getString(c.getColumnIndex(HPI.C_DATE_CREATED))));
            }while(c.moveToNext());
        }
        c.close();
        return HPIs;
    }


    public ArrayList<String> getGradeLevelsFromSchool(int schoolId) {
        ArrayList<String> gradeLevels = new ArrayList<String>();
        String sql = "SELECT DISTINCT r.grade_level " +
                " FROM tbl_record AS r, tbl_patient AS p " +
                " WHERE r.patient_id = p.patient_id " +
                " AND school_id = "+schoolId+
                " ORDER BY r.grade_level ASC";
        Cursor c =  getBetterDb.rawQuery(sql, null);
        if(c.moveToFirst()) {
            do {
                gradeLevels.add(c.getString(c.getColumnIndex(Record.C_GRADE_LEVEL)));
            } while(c.moveToNext());
        }
        return gradeLevels;
    }

    public boolean updateDatasetList(Dataset dataset, School school){
        ContentValues initialValues = new ContentValues();
        initialValues.put("dataset_id", dataset.getDatasetID());
        initialValues.put("status", 0); // the execution is different if _id is 2
        initialValues.put("school_id", dataset.getSchoolID());
        initialValues.put("date_created", dataset.getDateCreated());
        try{
            openDatabaseForWrite();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        int id = (int) getBetterDb.insertWithOnConflict("tbl_dataset", null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        //Log.d(TAG, "Number of Dataset:" + getAllDatasets().size());

        initialValues = new ContentValues();

        initialValues.put("school_id", school.getSchoolId());
        initialValues.put("name", school.getSchoolName());
        initialValues.put("municipality", school.getMunicipalityId());

        id = (int) getBetterDb.insertWithOnConflict("tbl_school", null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        closeDatabase();
        return true;
    }



    public void updateRecord(Record record){
        ContentValues values = new ContentValues();
        int row;
        values.put(Record.C_PATIENT_ID, record.getPatient_id());
        values.put(Record.C_DATE_CREATED, record.getDateCreated());
        values.put(Record.C_GRADE_LEVEL, record.getGradeLevel());
        values.put(Record.C_HEIGHT, record.getHeight());
        values.put(Record.C_WEIGHT, record.getWeight());
        values.put(Record.C_VISUAL_ACUITY_LEFT, record.getVisualAcuityLeft());
        values.put(Record.C_VISUAL_ACUITY_RIGHT, record.getVisualAcuityRight());
        values.put(Record.C_COLOR_VISION, record.getColorVision());
        values.put(Record.C_HEARING_LEFT, record.getHearingLeft());
        values.put(Record.C_HEARING_RIGHT, record.getHearingRight());
        values.put(Record.C_GROSS_MOTOR, record.getGrossMotor());
        values.put(Record.C_FINE_MOTOR_DOMINANT, record.getFineMotorDominant());
        values.put(Record.C_FINE_MOTOR_N_DOMINANT, record.getFineMotorNDominant());
        values.put(Record.C_FINE_MOTOR_HOLD, record.getFineMotorHold());
        values.put(Record.C_VACCINATION, record.getVaccination());
        values.put(Record.C_PATIENT_PICTURE, record.getPatientPicture());
        values.put(Record.C_REMARKS_STRING, record.getRemarksString());
        values.put(Record.C_REMARKS_AUDIO, record.getRemarksAudio());
        try {
            openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        row = (int) getBetterDb.replaceOrThrow(Record.TABLE_NAME, null, values);
        //Log.d(TAG, "replaceRecord Result: " + row);
        closeDatabase();
    }
    public void updateDatasetStatus(Dataset dataset){
        try {
            openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put("status", 1); // the execution is different if _id is 2
        getBetterDb.update("tbl_dataset", initialValues, "dataset_id = ?", new String[] {Integer.toString(dataset.getDatasetID())});  // number 1 is the _id here, update to variable for your code
    }

    public void updatePatient(Patient patient){
        ContentValues values = new ContentValues();
        int row;
        values.put(Patient.C_PATIENT_ID, patient.getPatientID());
        values.put(Patient.C_FIRST_NAME, patient.getFirstName());
        values.put(Patient.C_LAST_NAME, patient.getLastName());
        values.put(Patient.C_BIRTHDAY, patient.getBirthday());
        values.put(Patient.C_GENDER, patient.getGender());
        values.put(Patient.C_SCHOOL_ID, patient.getSchoolId());
        values.put(Patient.C_HANDEDNESS, patient.getHandedness());
        values.put(Patient.C_REMARKS_STRING, patient.getRemarksString());
        values.put(Patient.C_REMARKS_AUDIO, patient.getRemarksAudio());
        try {
            openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        row = (int) getBetterDb.replaceOrThrow(Patient.TABLE_NAME, null, values);
        Log.d(TAG, "replacePatient Result: " + row);
        closeDatabase();
    }
    public void updateSchools(School school){
        ContentValues values = new ContentValues();
        values.put(School.C_SCHOOL_ID, school.getSchoolId());
        values.put(School.C_MUNICIPALITY, school.getMunicipality());
        values.put(School.C_SCHOOLNAME, school.getSchoolName());
        try {
            openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int id = (int) getBetterDb.insertWithOnConflict("tbl_school", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /*public void updateMunicipalities(Municipality municipality){
        ContentValues values = new ContentValues();
        values.put(Municipality.C_MUNICIPALITY_ID, municipality);
        values.put(School.C_SCHOOL_ID, school.getSchoolId());
        values.put(School.C_MUNICIPALITY, school.getMunicipality());
        values.put(School.C_SCHOOLNAME, school.getSchoolName());
        try {
            openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int id = (int) getBetterDb.insertWithOnConflict("tbl_school", null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }*/

    public int getSchoolCount(){
        int count = 0;
        try {
            openDatabaseForRead();
        }catch(SQLException e){
            e.printStackTrace();
            Log.e(TAG, "Error Opening Database! D:");
        }
        Cursor c = getBetterDb.rawQuery("SELECT COUNT(*) FROM   tbl_school AS schoolCount", null);
        if(c.moveToFirst()){
            do{
                count = c.getInt(0);
            }while(c.moveToNext());
        }
        c.close();
        return count;
    }

    public String getSchoolName(int schoolID){
        String schoolName = "";
        try {
            openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor c = getBetterDb.rawQuery("SELECT name FROM tbl_school WHERE school_id = ? ", new String[] {Integer.toString(schoolID)});
        if(c.moveToFirst())
            schoolName = c.getString(0);
        closeDatabase();
        return schoolName;
    }

    public ArrayList<Patient> getPatientsWithCondition(int schoolId, String date, String column, String value) {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor c;
        if(column.contains("motor")) {
            if(column.contains("hold")) {
                c = getBetterDb.rawQuery("SELECT * "
                        +" FROM "+Record.TABLE_NAME+" AS r, "+Patient.TABLE_NAME+" AS p"
                        +" WHERE r."+Patient.C_PATIENT_ID+" = p."+Record.C_PATIENT_ID
                        +" AND p."+Patient.C_SCHOOL_ID+" = "+schoolId
                        +" AND r."+Record.C_DATE_CREATED+" LIKE '"+date+"' "
                        +" AND r."+column+" = "+ ValueCounter.ConvertHold(value)
                        +" GROUP BY p.patient_id; ", null);
            } else {
                c = getBetterDb.rawQuery("SELECT * "
                        + " FROM " + Record.TABLE_NAME + " AS r, " + Patient.TABLE_NAME + " AS p"
                        + " WHERE r." + Patient.C_PATIENT_ID + " = p." + Record.C_PATIENT_ID
                        + " AND p." + Patient.C_SCHOOL_ID + " = " + schoolId
                        +" AND r."+Record.C_DATE_CREATED+" LIKE '"+date+"' "
                        + " AND r." + column + " = " + ValueCounter.ConvertMotor(value)
                        + " GROUP BY p.patient_id; ", null);
            }
        } else {
            c = getBetterDb.rawQuery("SELECT * "
                    +" FROM "+Record.TABLE_NAME+" AS r, "+Patient.TABLE_NAME+" AS p"
                    +" WHERE r."+Patient.C_PATIENT_ID+" = p."+Record.C_PATIENT_ID
                    +" AND p."+Patient.C_SCHOOL_ID+" = "+schoolId
                    +" AND r."+Record.C_DATE_CREATED+" LIKE '"+date+"' "
                    +" AND r."+column+" LIKE '"+value+"' "
                    +" GROUP BY p.patient_id; ", null);
        }
        if(c.moveToFirst()){
            do{
                patients.add(new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                        c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                        c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO))));
            }while(c.moveToNext());
        }
        c.close();

        return patients;
    }

    /**
     * Get the ideal value which has an record column of
     * {@code recordColumn}, gender {@code gender}, age in years {@code age}.
     * @param recordColumn column of the ideal value to be queried.
     * @param gender gender of the patient
     * @param age age in years of the patient
     * @return the ideal growth value of the patient.
     */
    public IdealValue getIdealValue(String recordColumn, int gender, int age){
        IdealValue idealValue = null;

        Cursor c = getBetterDb.query(IdealValue.TABLE_NAME, null,
                IdealValue.C_RECORD_COLUMN+" LIKE '"+recordColumn
                +"' AND "+IdealValue.C_GENDER+" = "+gender
                +" AND "+IdealValue.C_YEAR+" = "+age, null, null, null, null);
        if(c.moveToFirst()){
            //do{
                idealValue = new IdealValue(c.getInt(c.getColumnIndex(IdealValue.C_GROWTH_ID)),
                        c.getString(c.getColumnIndex(IdealValue.C_RECORD_COLUMN)),
                        c.getInt(c.getColumnIndex(IdealValue.C_GENDER)),
                        c.getInt(c.getColumnIndex(IdealValue.C_YEAR)),
                        c.getInt(c.getColumnIndex(IdealValue.C_MONTH)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_N3SD)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_N2SD)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_N1SD)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_MEDIAN)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_P1SD)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_P2SD)),
                        c.getFloat(c.getColumnIndex(IdealValue.C_P3SD)));
            //}while(c.moveToNext());
            idealValue.printIdealValue();
        }
        c.close();

        return idealValue;
    }

    public Record getAverageRecord(Patient patient, String recordDate) {
        Record record = null;
        String recordYear, birthYear;
        recordYear = recordDate.substring(recordDate.length()-4);
        birthYear = patient.getBirthday().substring(patient.getBirthday().length()-4);

        String sql = "SELECT AVG(r.height), AVG(r.weight) " +
                "FROM tbl_record AS r, tbl_patient AS p " +
                "WHERE r.patient_id = p.patient_id " +
                "AND r.date_created LIKE '%"+recordYear+"'" +
                "AND p.birthday LIKE '%"+birthYear+"'";

        Cursor c = getBetterDb.rawQuery(sql, null);
        Log.v(TAG, "recordYear = "+recordYear);
        Log.v(TAG, "birthYear = "+birthYear);
        if(c.moveToFirst()) {
            record = new Record();
            record.setHeight(c.getDouble(0));
            record.setWeight(c.getDouble(1));
        }

        String[] recordColumns = {Record.C_VISUAL_ACUITY_LEFT, Record.C_VISUAL_ACUITY_RIGHT,
                Record.C_COLOR_VISION, Record.C_HEARING_LEFT, Record.C_HEARING_RIGHT,
                Record.C_GROSS_MOTOR, Record.C_FINE_MOTOR_DOMINANT, Record.C_FINE_MOTOR_N_DOMINANT,
                Record.C_FINE_MOTOR_HOLD};

        for(int i = 0; i < recordColumns.length; i++) {
            c = getMostFrequentValue(birthYear, recordYear, recordColumns[i]);
            if(c.moveToFirst()) {
                switch (recordColumns[i]) {
                    case Record.C_VISUAL_ACUITY_LEFT:
                        record.setVisualAcuityLeft(c.getString(0));
                        break;
                    case Record.C_VISUAL_ACUITY_RIGHT:
                        record.setVisualAcuityRight(c.getString(0));
                        break;
                    case Record.C_COLOR_VISION:
                        record.setColorVision(c.getString(0));
                        break;
                    case Record.C_HEARING_LEFT:
                        record.setHearingLeft(c.getString(0));
                        break;
                    case Record.C_HEARING_RIGHT:
                        record.setHearingRight(c.getString(0));
                        break;
                    case Record.C_GROSS_MOTOR:
                        record.setGrossMotor(c.getInt(0));
                        break;
                    case Record.C_FINE_MOTOR_DOMINANT:
                        record.setFineMotorDominant(c.getInt(0));
                        break;
                    case Record.C_FINE_MOTOR_N_DOMINANT:
                        record.setFineMotorDominant(c.getInt(0));
                        break;
                    case Record.C_FINE_MOTOR_HOLD:
                        record.setFineMotorDominant(c.getInt(0));
                        break;
                }
            }
        }

        return record;
    }

    private Cursor getMostFrequentValue(String birthYear, String recordYear, String recordColumn) {
        String sql = "SELECT ?, COUNT(*)" +
                "FROM tbl_record AS r, tbl_patient AS p " +
                "WHERE r.patient_id = p.patient_id " +
                "AND r.date_created LIKE '%"+recordYear+"' " +
                "AND p.birthday LIKE '%"+birthYear+"' " +
                "GROUP BY ? " +
                "ORDER BY count(*) DESC " +
                "LIMIT 1";

        String[] selectionArgs = {"r."+recordColumn, "r."+recordColumn};

        Cursor c = getBetterDb.rawQuery(sql, selectionArgs);

        return c;
    }

    public String getMunicipalityName(int citymunCode) {
        String municipalityName = "";
        try {
            openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT citymunDesc FROM tbl_municipality WHERE citymunCode = ? ";
        Cursor c = getBetterDb.rawQuery(query, new String[]{Integer.toString(citymunCode)});
        if (c.moveToFirst())
            municipalityName = c.getString(0);
        closeDatabase();
        return municipalityName;
    }
    private ArrayList<PatientRecord> getPatientRecords(String sql) {
        ArrayList<PatientRecord> records = new ArrayList<>();
        int gender;
        String birthday;
        Cursor c = getBetterDb.rawQuery(sql, null);
        if(c.moveToFirst()) {
            do{
                Record record = new Record(c.getInt(c.getColumnIndex(Record.C_RECORD_ID)), c.getInt(c.getColumnIndex(Record.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Record.C_DATE_CREATED)), /*c.getString(c.getColumnIndex(Record.C_GRADE_LEVEL)),*/
                        c.getDouble(c.getColumnIndex(Record.C_HEIGHT)),
                        c.getDouble(c.getColumnIndex(Record.C_WEIGHT)), c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_LEFT)),
                        c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_RIGHT)), c.getString(c.getColumnIndex(Record.C_COLOR_VISION)),
                        c.getString(c.getColumnIndex(Record.C_HEARING_LEFT)), c.getString(c.getColumnIndex(Record.C_HEARING_RIGHT)),
                        c.getInt(c.getColumnIndex(Record.C_GROSS_MOTOR)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_N_DOMINANT)),
                        c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_DOMINANT)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_HOLD)),
                        c.getBlob(c.getColumnIndex(Record.C_VACCINATION)), c.getBlob(c.getColumnIndex(Record.C_PATIENT_PICTURE)),
                        c.getString(c.getColumnIndex(Record.C_REMARKS_STRING)), c.getBlob(c.getColumnIndex(Record.C_REMARKS_AUDIO)));
                gender = c.getInt(c.getColumnIndex(Patient.C_GENDER));
                birthday = c.getString(c.getColumnIndex(Patient.C_BIRTHDAY));
                PatientRecord patientRecord = new PatientRecord(gender, birthday, record);
                //record.printRecord();
                //Log.v(TAG, "Gender: "+gender);
                //Log.v(TAG, "Birthday: "+birthday);
                records.add(patientRecord);
            } while(c.moveToNext());
        } else {
            Log.v(TAG, "National Values is empty!");
        }
        return records;
    }

    public ArrayList<PatientRecord> getAllRecordsOnYear(String recordYear) {
        String sql = "SELECT * "
                +" FROM "+School.TABLE_NAME+" AS s, "+Record.TABLE_NAME+" AS r, "+Patient.TABLE_NAME+" AS p "
                +" WHERE s."+School.C_SCHOOL_ID+" = p."+Patient.C_SCHOOL_ID
                +" AND r."+Record.C_PATIENT_ID+" = p."+Patient.C_PATIENT_ID
                +" AND "+Record.C_DATE_CREATED+" LIKE '%"+recordYear+"' "
                +" GROUP BY r."+Record.C_PATIENT_ID;

        return getPatientRecords(sql);
    }

    public ArrayList<PatientRecord> getRecordsFromMunicipalityOnYear(int schoolId, String recordYear)  {
        String sql = "SELECT * "
                +" FROM "+School.TABLE_NAME+" AS s, "+Record.TABLE_NAME+" AS r, "+Patient.TABLE_NAME+" AS p "
                +" WHERE s."+School.C_SCHOOL_ID+" = p."+Patient.C_SCHOOL_ID
                +" AND r."+Record.C_PATIENT_ID+" = p."+Patient.C_PATIENT_ID
                +" AND r."+Record.C_DATE_CREATED+" LIKE '%"+recordYear+"' "
                +" AND s."+School.C_MUNICIPALITY+" = (SELECT "+School.C_MUNICIPALITY
                                                    +" FROM "+School.TABLE_NAME
                                                    +" WHERE "+School.C_SCHOOL_ID+" = "+schoolId+") "
                +" GROUP BY r."+Record.C_PATIENT_ID;

        return getPatientRecords(sql);
    }

    public ArrayList<PatientRecord> getRecordsFromProvinceOnYear(int schoolId, String recordYear)  {
        String sql = "SELECT * "
                +" FROM "+Patient.TABLE_NAME+" AS p, "+Record.TABLE_NAME+" AS r "
                +" WHERE p."+Patient.C_PATIENT_ID+" = r."+Record.C_PATIENT_ID
                +" AND r."+Record.C_DATE_CREATED+" LIKE '%"+recordYear+"' "
                +" AND p."+Patient.C_SCHOOL_ID+" IN (SELECT "+School.C_SCHOOL_ID
                    +" FROM "+School.TABLE_NAME
                    +" WHERE "+School.C_MUNICIPALITY+" IN (SELECT "+Municipality.C_MUNICIPALITY_ID
                        +" FROM "+Municipality.TABLE_NAME
                        +" WHERE "+Municipality.C_PROVINCE_ID+" IN (SELECT m."+Municipality.C_PROVINCE_ID
                        +" FROM tbl_school AS s, tbl_municipality AS m"
                        +" WHERE s.municipality = m.municipality_id\n"
                        +" AND school_id = "+schoolId+")))";

        return getPatientRecords(sql);
    }

    public ArrayList<PatientRecord> getRecordsFromRegionOnYear(int schoolId, String recordYear)  {
        String sql = "SELECT *\n" +
                "FROM tbl_patient AS p, tbl_record AS r \n" +
                "WHERE p.patient_id = r.patient_id \n" +
                "AND r.date_created LIKE '%"+recordYear+"' \n" +
                "AND p.school_id IN (SELECT s.school_id  \n" +
                "FROM tbl_school AS s \n" +
                "WHERE s.municipality IN (SELECT m.municipality_id\n" +
                "\tFROM tbl_province AS p, tbl_municipality AS m \n" +
                "\tWHERE m.province_id = p.province_id\n" +
                "\tAND p.region_id IN (SELECT p.region_id\n" +
                "\t\tFROM tbl_school AS s, tbl_municipality AS m, \n" +
                "\t\ttbl_province AS p\n" +
                "\t\tWHERE s.municipality = m.municipality_id\n" +
                "\t\tAND p.province_id = m.province_id\n" +
                "\t\tAND school_id = "+schoolId+")))";

        return getPatientRecords(sql);
    }



    public Map<String, Integer> getNationalValues(String recordColumn, String recordYear) {
        Map<String, Integer> distribution = new HashMap<String, Integer>();
        String sql = "SELECT "+recordColumn+", COUNT(*) AS num" +
                " FROM tbl_record " +
                " WHERE "+Record.C_DATE_CREATED + " LIKE '%"+recordYear+"' "+
                " GROUP BY "+recordColumn;

        Cursor c = getBetterDb.rawQuery(sql, null);
        if(c.moveToFirst()) {
            do{
                distribution.put(c.getString(c.getColumnIndex(recordColumn)), c.getInt(c.getColumnIndex("num")));
            } while(c.moveToNext());
        } else {
            Log.v(TAG, "National Values is empty!");
        }
        return distribution;
    }

    public Map<String, Integer> getDatasetValues(int schoolId, String date, String recordColumn) {
        Map<String, Integer> distribution = new HashMap<String, Integer>();
        String sql = "SELECT "+recordColumn+", COUNT(*) AS num" +
                " FROM tbl_record AS r, tbl_patient AS p " +
                " WHERE r.patient_id = p.patient_id " +
                " AND p.school_id = "+schoolId+" AND r.date_created LIKE '"+date+"' " +
                " GROUP BY r."+recordColumn;

        Cursor c = getBetterDb.rawQuery(sql, null);
        if(c.moveToFirst()) {
            do{
                distribution.put(c.getString(c.getColumnIndex(recordColumn)), c.getInt(c.getColumnIndex("num")));
            } while(c.moveToNext());
        } else {
            Log.v(TAG, "Dataset Values is empty!");
        }
        return distribution;
    }
}