package com.example.biztrack.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object BizTrackMigrations {

    val MIGRATION_2_3 = object : Migration(2, 3) {

        override fun migrate(db: SupportSQLiteDatabase) {

            // Add account ownership to existing products.
            db.execSQL(
                """
                ALTER TABLE products
                ADD COLUMN userId TEXT NOT NULL DEFAULT 'legacy_unclaimed'
                """
            )

            // Add account ownership to existing transactions.
            db.execSQL(
                """
                ALTER TABLE transactions
                ADD COLUMN userId TEXT NOT NULL DEFAULT 'legacy_unclaimed'
                """
            )

            // Create the users table.
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT NOT NULL,
                    fullName TEXT NOT NULL,
                    businessName TEXT NOT NULL,
                    email TEXT NOT NULL,
                    passwordHash TEXT NOT NULL,
                    passwordSalt TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """
            )

            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_users_email
                ON users(email)
                """
            )

            // Rebuild budgets to support separate accounts.
            db.execSQL(
                """
                CREATE TABLE budgets_new (
                    userId TEXT NOT NULL,
                    category TEXT NOT NULL,
                    allocatedAmount REAL NOT NULL,
                    period TEXT NOT NULL,
                    PRIMARY KEY(userId, category, period)
                )
                """
            )

            // Preserve the existing budget.
            db.execSQL(
                """
                INSERT INTO budgets_new (
                    userId, category, allocatedAmount, period
                )
                SELECT
                    'legacy_unclaimed', category, allocatedAmount, period
                FROM budgets
                """
            )

            db.execSQL("DROP TABLE budgets")
            db.execSQL("ALTER TABLE budgets_new RENAME TO budgets")
        }
    }
}