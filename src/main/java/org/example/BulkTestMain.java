package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static org.example.bulkquerytest.TestCase.*;

@Slf4j
public class BulkTestMain {

    @SneakyThrows
    public static void main(String[] args) {
        Class.forName("org.postgresql.Driver");

        test1();

//        test2();

//        test3();

//        test4();

//        test5();

//        test6();

//        test7();

//        test8();
    }
}