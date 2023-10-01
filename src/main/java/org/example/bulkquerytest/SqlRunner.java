package org.example.bulkquerytest;

import java.sql.Statement;

@FunctionalInterface
interface SqlRunner {
    void runner(Statement stmt) throws Exception;
}
