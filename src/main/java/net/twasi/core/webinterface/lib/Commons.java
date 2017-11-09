package net.twasi.core.webinterface.lib;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.webinterface.dto.ApiDTO;
import net.twasi.core.webinterface.dto.error.BadRequestDTO;
import net.twasi.core.webinterface.dto.error.UnallowedMethodDTO;
import org.bson.Document;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Commons {

    public static void handleUnallowedMethod(HttpExchange t) {
        writeDTO(t, new UnallowedMethodDTO(), 405);
    };

    public static void writeString(HttpExchange t, String s, int code) {
        try {
            t.sendResponseHeaders(code, s.length());
            OutputStream os = t.getResponseBody();
            os.write(s.getBytes());
            os.close();
        } catch (IOException e) {
            TwasiLogger.log.error(e);
        }
    }

    @Deprecated
    public static void writeDocument(HttpExchange t, Document doc, int code) {
        String json = doc.toJson();
        t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        t.getResponseHeaders().set("Content-Type", "application/json");
        writeString(t, json, code);
    }

    public static void writeDTO(HttpExchange t, ApiDTO dto, int code) {
        String json = new Gson().toJson(dto);
        t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        t.getResponseHeaders().set("Content-Type", "application/json");
        writeString(t, json, code);
    }

    public static void writeRedirect(HttpExchange t, String redirectTo) {
        t.getResponseHeaders().set("Location", redirectTo);
        writeString(t, "<a href='" + redirectTo + "'>Click here</a>", 307);
    }

    public static void handleBadRequest(HttpExchange t) {
        writeDTO(t, new BadRequestDTO(), 400);
    }

    public static HashMap<String, String> parseQueryParams(HttpExchange t) {
        HashMap<String, String> params = new HashMap<>();
        String queryString = t.getRequestURI().getQuery();
        String[] pairs = queryString.split("&");

        for (String str : pairs) {
            String[] splittedString = str.split("=", 2);
            if (splittedString.length != 2) {
                params.put(splittedString[0], "");
                continue;
            }

            params.put(splittedString[0], splittedString[1]);
        }
        return params;
    }
}