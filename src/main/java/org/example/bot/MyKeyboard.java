package org.example.bot;

import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.additional.buttons.*;
import com.google.gson.JsonObject;
import org.example.status.BotStatus;
import org.example.utils.JsonWorker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class MyKeyboard {
    private static Map<BotStatus, Keyboard> keys;
    public static void init(){
        keys = new HashMap<>();
        JSONArray arr = JsonWorker.getArray();
        for(BotStatus status: BotStatus.values()) {
            JSONObject obj = (JSONObject) ((JSONObject) arr.stream()
                    .filter(elem -> ((JSONObject) elem).containsKey(status.toString()))
                    .toList().get(0)).get(status.toString());
            keys.put(status, getKeyboard(obj));
        }
    }

    public static Keyboard getKeyboard(BotStatus status) {
        return keys.get(status);
    }
    private static List<Button> getButtons(JSONObject obj){
        List<Button> list = new ArrayList<>();
        Button but0;
        Button but1;
        System.out.println(obj);
        addToList(obj, "0", list);
        addToList(obj, "1", list);
        return list;
    }

    private static JsonObject initJson(JSONObject obj) {
        JsonObject json = new JsonObject();
        if(obj.get("answer") != null && obj.get("answer").toString().length() > 0)
            json.addProperty("answer", obj.get("answer").toString());
        if(obj.get("function_name") != null && obj.get("function_name").toString().length() > 0)
            json.addProperty("function_name", obj.get("function_name").toString());
        if(obj.get("next_status") != null && obj.get("next_status").toString().length() > 0)
            json.addProperty("next_status", obj.get("next_status").toString());
        if(obj.get("key") != null && obj.get("key").toString().length() > 0)
            json.addProperty("key", obj.get("key").toString());
        return json;
    }

    private static void addToList(JSONObject obj, String str, List<Button> list) {
        Button but0;
        if(obj != null && obj.get(str).toString().length() > 0) {
            JSONObject obj1 = (JSONObject) obj.get(str);
            if(obj1.get("color") != null && obj1.get("text") != null
                    && obj1.get("color").toString().length() > 0
                    && obj1.get("text").toString().length() > 0) {
                JsonObject json = initJson(obj1);
                but0 = new TextButton(Arrays.stream(Button.Color.values())
                        .filter(elem -> elem.toString().equals(obj1.get("color").toString()))
                        .toList().get(0),
                        new TextButton.Action(obj1.get("text").toString(),
                                json));
                list.add(but0);
            }
        }
    }
    private static Keyboard getKeyboard(JSONObject lines){

        JSONObject line0 = (JSONObject) lines.get("0Line");
        JSONObject line1 = (JSONObject) lines.get("1Line");
        JSONObject line2 = (JSONObject) lines.get("2Line");
        List<Button> buttons = getButtons(line0);
        List<List<Button>> result = new ArrayList<>();
        if(!buttons.isEmpty())
            result.add(buttons);
        buttons = getButtons(line1);
        if(!buttons.isEmpty())
            result.add(buttons);
        buttons = getButtons(line2);
        if(!buttons.isEmpty())
            result.add(buttons);

        Keyboard keyboard = new Keyboard(result);

        return keyboard;
    }
}