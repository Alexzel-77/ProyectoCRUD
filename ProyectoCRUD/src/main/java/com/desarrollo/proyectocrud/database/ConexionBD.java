package com.desarrollo.proyectocrud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/bdventas";

    private static final String USUARIO = "postgres";

    private static final String CONTRASENA = "12345678";

    public static Connection conectar() {

        Connection conexion = null;

        try {

            conexion = DriverManager.getConnection(
                    URL,
                    USUARIO,
                    CONTRASENA
            );

            System.out.println("Conexion exitosa a PostgreSQL");

        } catch (SQLException e) {

            System.out.println("Error al conectar con PostgreSQL");
            System.out.println(e.getMessage());
        }

        return conexion;
    }
}