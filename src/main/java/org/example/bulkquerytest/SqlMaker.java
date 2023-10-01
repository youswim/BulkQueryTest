package org.example.bulkquerytest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlMaker {

    public static String insertSqlString(String id) {
        return "INSERT INTO USERS VALUES ('" + id + "', 0, 0, 0);";
    }

    public static String bulkInsertSqlString(int startIdx, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO USERS VALUES");
        for (long i = startIdx; i < startIdx + size; i++) {
            sb.append("('TEST" + String.format("%07d", i) + "',0,0,0),");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        log.info("쿼리 생성 완료");
        return sb.toString();
    }

    public static String upsertSqlString(String id) {
        return String.format(
                """
                        insert into USERS values ('%s', '1','1','1')
                        on conflict (id)
                        do update set term1 = EXCLUDED.term1, term2 = EXCLUDED.term2, term3 = EXCLUDED.term3;
                """,
                id
        );
    }

    public static String bulkUpsertSqlString(int startIdx, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO USERS (id, term1, term2, term3) values ");
        for (int i = startIdx; i < size + startIdx; i++) {
            sb.append("('TEST" + String.format("%07d", i) + "', 1, 1, 1),");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ");
        sb.append("ON CONFLICT (id) do update set term1 = excluded.term1, term2 = excluded.term2, term3 = excluded.term3;");
        log.info("쿼리 생성 완료");
        return sb.toString();
    }

    public static String updateAllSqlString() {
        return "UPDATE USERS SET term1 = 0, term2 = 0, term3 = 0;";
    }

    public static String updateSqlString(String id) {
        return "UPDATE USERS SET term1 = 0, term2 = 0, term3 = 0 WHERE id = '" + id + "';";
    }

    public static String deleteSqlString(String id) {
        return "DELETE FROM USERS WHERE id = '" + id + "';";
    }

    public static String deleteAllSqlString() {
        return "DELETE FROM USERS;";
    }

    public static String deleteMilStartUsers() {
        return "DELETE FROM USERS WHERE id LIKE 'TEST1%'";
    }

    public static String makeSelectUsers(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM USERS WHERE id IN (");
        for (int i = 0; i < size; i++) {
            sb.append("'TEST" + String.format("%07d", i) + "',");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        log.info("쿼리 생성 완료");
        return sb.toString();
    }
}
