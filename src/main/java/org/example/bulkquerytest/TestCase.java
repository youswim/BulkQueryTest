package org.example.bulkquerytest;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Slf4j
public class TestCase {

    private static final int size = 1_000_000;
    private static final int repeatNum = 10;

    private static final int sleepTime = 1000 * 60 * 2;

    private static final int shortSleepTime = 1000 * 10;

    private static final String url = "jdbc:postgresql://localhost:5432/postgres";

    private static final String user = System.getenv("USERNAME"); // 환경변수로 등록!

    private static final String password = System.getenv("PASSWORD");

    public static void test1() { // 아무것도 존재하지 않는 상태에서 size건의 BULK INSERT
        sendSqlTemplate(
                stmt -> stmt.executeUpdate(SqlMaker.deleteAllSqlString()),
                stmt -> stmt.execute(SqlMaker.bulkInsertSqlString(0, size)),
                stmt -> stmt.executeUpdate(SqlMaker.deleteAllSqlString()),
                repeatNum,
                sleepTime
        );
    }

    public static void test2() { // size 개의 데이터가 db에 존재할 때 size건의 BULK INSERT
        sendSqlTemplate(
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.bulkInsertSqlString(size, size)),
                stmt -> stmt.executeUpdate(SqlMaker.deleteMilStartUsers()),
                repeatNum,
                sleepTime

        );
    }

    public static void test3() { // size 개의 데이터가 db에 존재할 때, UPSERT
        sendSqlTemplate(
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.bulkUpsertSqlString(size / 2, size)),
                stmt -> {stmt.executeUpdate(SqlMaker.deleteMilStartUsers()); stmt.executeUpdate(SqlMaker.updateAllSqlString());},
                repeatNum,
                sleepTime
        );
    }

    public static void test4() {
        sendSqlTemplate( // size 개의 데이터가 db에 존재할 때 SELECT
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.makeSelectUsers(size / 2)),
                stmt -> {},
                repeatNum,
                sleepTime
        );
    }

    // 개별 INSERT
    public static void test5() {
        sendSqlTemplate( // 개별 INSERT
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.insertSqlString("TEST3000000")),
                stmt -> stmt.executeUpdate(SqlMaker.deleteSqlString("TEST3000000")),
                repeatNum,
                shortSleepTime
        );
    }

    // 개별 UPDATE
    public static void test6() {
        sendSqlTemplate( // 개별 UPDATE
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.executeUpdate(SqlMaker.updateSqlString("TEST0000000")),
                stmt -> {},
                repeatNum,
                shortSleepTime
        );
    }

    // 개별 UPSERT(INSERT)
    public static void test7() {
        sendSqlTemplate( // 개별 UPSERT (INSERT)
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.upsertSqlString("TEST3000000")),
                stmt -> stmt.execute(SqlMaker.deleteSqlString("TEST3000000")),
                repeatNum,
                shortSleepTime
        );
    }


    public static void test8() {
        sendSqlTemplate( // 개별 UPSERT(UPDATE)
                stmt -> {stmt.executeUpdate(SqlMaker.deleteAllSqlString()); stmt.execute(SqlMaker.bulkInsertSqlString(0, size));},
                stmt -> stmt.execute(SqlMaker.upsertSqlString("TEST0000000")),
                stmt -> {},
                repeatNum,
                shortSleepTime
        );
    }

    public static void sendSqlTemplate(SqlRunner initRunner, SqlRunner mainRunner, SqlRunner closeRunner, int repeatCnt, int sleepTime) {

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            initRunner.runner(stmt);
            conn.close();
            log.info("초기화 완료");
            Thread.sleep(sleepTime);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        for (int i = 0; i < repeatCnt; i++) {
            log.info("i : {}", i);
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement(); // DB 커넥션 생성


                long beforeTime = System.nanoTime(); // 시작 시간
                mainRunner.runner(stmt);
                long afterTime = System.nanoTime(); // 종료 시간

                log.info("시간차이(ns) : {}", (afterTime - beforeTime));
                conn.close();
                Thread.sleep(sleepTime); // 2분간 휴동

            } catch (Exception e) {
                log.error(e.getMessage());
            }

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                closeRunner.runner(stmt);
                conn.close();
                Thread.sleep(sleepTime); // 2분간 휴동
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
