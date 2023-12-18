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
        if(obj != null && obj.get("0").toString().length() > 0) {
            but0 = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
                    obj.get("0").toString(),
                    new JsonObject()));
            list.add(but0);
        }
        if(obj != null && obj.get("1").toString().length() > 0) {
            but1 = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
                    obj.get("1").toString(),
                    new JsonObject()));
            list.add(but1);
        }
        return list;
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
//
//    //Menu
//    private static final Button getFilesFromYandex = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Начать",
//            new JsonObject()));
//    //status
//    private static final Button applicant = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Абитуриент",
//            new JsonObject()));
//    private static final Button student = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Студент",
//            new JsonObject()));
//
//    //greetings
//    private static final Button writeToCommunity = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Написать сообществу",
//            new JsonObject()));
//    private static final Button faq = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Частые вопросы",
//            new JsonObject()));
//    private static final Button schedule = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Расписание",
//            new JsonObject()));
//    private static final Button settings = new TextButton(Button.Color.NEGATIVE, new TextButton.Action(
//            "Настройки",
//            new JsonObject()));
//
//    //mainForApplicant
//    private static final Button getAConsultation = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Получить консультацию",
//            new JsonObject()));
//    /*faq*/
//    /*settings*/
//
//    //faq-student
//    //new status
//    private static final Button iAmStudent = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Я студент\uD83D\uDC9A",
//            new JsonObject()));
//    private static final Button iAmApplicant = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Я абитуриент",
//            new JsonObject()));
//    private static final Button changeStatus = new TextButton(Button.Color.NEGATIVE, new TextButton.Action(
//            "Изменить статус",
//            new JsonObject()));
//
//    private static final Button stipend = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Стипендия и льготы",
//            new JsonObject()));
//    private static final Button studentStatus = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Изменение статуса студента",
//            new JsonObject()));
//    private static final Button referencesAndBSC = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Справки и БСК",
//            new JsonObject()));
//    private static final Button directorate = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Дирекция и контакты",
//            new JsonObject()));
//    private static final Button hostel = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Общежития",
//            new JsonObject()));
//    private static final Button persDepartment = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Студенческий отдел кадров",
//            new JsonObject()));
//    private static final Button usefulGroups = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Полезные ссылочки",
//            new JsonObject()));
//    private static final Button schoolGroups = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Высшие школы",
//            new JsonObject()));
//
//    //schedule
//    private static final Button findTeacher = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Найти преподавателя",
//            new JsonObject()));
//    private static final Button lastGroup = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Последняя группа",
//            new JsonObject()));
//
//    //teachSearch
//
//    private static final Button findTeacherSchedule = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Расписание преподавателя",
//            new JsonObject()));
//    private static final Button findTeacherCard = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Контакты преподавателя",
//            new JsonObject()));
//
//    //settings
//    private static final Button subscribe = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Подписаться на рассылку",
//            new JsonObject()));
//    private static final Button unSubscribe = new TextButton(Button.Color.NEGATIVE, new TextButton.Action(
//            "Отписаться от рассылки",
//            new JsonObject()));
//    private static final Button makeNews = new TextButton(Button.Color.PRIMARY, new TextButton.Action( // for admin
//            "Создать новость",
//            new JsonObject()));
//    private static final Button support = new TextButton(Button.Color.NEGATIVE, new TextButton.Action(
//            "Поддержка",
//            new JsonObject()));
//    private static final Button enableConsultation = new TextButton(Button.Color.POSITIVE, new TextButton.Action(
//            "Включить консультацию",
//            new JsonObject()));
//    private static final Button disableConsultation = new TextButton(Button.Color.NEGATIVE, new TextButton.Action(
//            "Выключить консультацию",
//            new JsonObject()));
//
//    //newsSend
//    private static final Button publish = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Опубликовать",
//            new JsonObject()));
//    //work buttons
//    private static final Button back = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Назад",
//            new JsonObject()));
//    private static final Button cancel = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Отмена",
//            new JsonObject()));
//    private static final Button finishDialogue = new TextButton(Button.Color.PRIMARY, new TextButton.Action(
//            "Завершить диалог",
//            new JsonObject()));
//
//    private static final Button VIESH = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://vk.com/im?media=&sel=-60780682",
//            "ВИЭШ"
//            ));
//    private static final Button VSHSiT = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://vk.com/im?media=&sel=-67851131",
//            "ВШСиТ"
//            ));
//    private static final Button VSHPM = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://vk.com/im?media=&sel=-205521720",
//            "ВШПМ"
//            ));
//    private static final Button VSHBI = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://vk.com/im?media=&sel=-63100084",
//            "ВШБИ"
//            ));
//    private static final Button VSHAU = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://vk.com/im?media=&sel=-209576595",
//            "ВШАУ"
//            ));
//
//    private static final Button tipsForApplicants = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://www.spbstu.ru/abit/faq/",
//            "Советы абитуриентам"
//    ));
//    private static final Button undergraduate = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://www.spbstu.ru/abit/bachelor/",
//            "Бакалавриат"
//    ));
//    private static final Button master = new OpenLinkButton(new OpenLinkButton.Action(
//            "https://www.spbstu.ru/abit/master/",
//            "Магистратура"
//    ));
//
//    private static final Button contacts = new TextButton(Button.Color.SECONDARY, new TextButton.Action(
//            "Контакты",
//            new JsonObject()));
//
//
//
//    //----------------------------------------------------------------------------------------------------
//    private static final List<Button> schedulePlusSettings = Arrays.asList(schedule, settings);
//    private static final List<Button> stipendPlusReferencesAndBSC = Arrays.asList(stipend, referencesAndBSC);
//    private static final List<Button> studentStatusPlusDirectorate = Arrays.asList(studentStatus, directorate);
//    private static final List<Button> hostelPlusPersDepartment = Arrays.asList(hostel, persDepartment);
//    private static final List<Button> usefulGroupsPlusSchoolGroups = Arrays.asList(usefulGroups, schoolGroups);
//    private static final List<Button> changeStatusPlusSupport = Arrays.asList(changeStatus, support);
//    private static final List<Button> VIESHplusVSHSiT = Arrays.asList(VIESH, VSHSiT);
//    private static final List<Button> VSHPMplusVSHBI = Arrays.asList(VSHPM, VSHBI);
//    private static final List<Button> VSHAUplusBack = Arrays.asList(VSHAU, back);
//    private static final List<Button> undergraduatePlusMaster = Arrays.asList(undergraduate, master);
//
//    //-----------------------------------------------------------------------------------------------------
//    private static final Keyboard keyboardEmpty = new Keyboard(List.of());
//    public static final Keyboard keyboardStart = new Keyboard(List.of(Collections.singletonList(start)));
//    private static final Keyboard keyboardMain = new Keyboard(Arrays.asList(
//            Collections.singletonList(writeToCommunity),
//            Collections.singletonList(faq),
//            schedulePlusSettings
//    ));
//    private static final Keyboard keyboardMainForApplicantWithEnabledConsultation = new Keyboard(Arrays.asList(
//            Collections.singletonList(getAConsultation),
//            Collections.singletonList(faq),
//            Collections.singletonList(settings)
//    ));
//    private static final Keyboard keyboardMainForApplicantWithDisabledConsultation = new Keyboard(Arrays.asList(
//            Collections.singletonList(faq),
//            Collections.singletonList(settings)
//    ));
//    private static final Keyboard keyboardStatus = new Keyboard(Arrays.asList(
//            Collections.singletonList(applicant),
//            Collections.singletonList(student)
//    ));
//    private static final Keyboard keyboardFAQForStudent = new Keyboard(Arrays.asList(
//            stipendPlusReferencesAndBSC,
//            studentStatusPlusDirectorate,
//            hostelPlusPersDepartment,
//            usefulGroupsPlusSchoolGroups,
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardBack = new Keyboard(List.of(Collections.singletonList(back)));
//    private static final Keyboard keyboardSettingsForUnsubscribedUser = new Keyboard(Arrays.asList(
//            Collections.singletonList(subscribe),
//            changeStatusPlusSupport,
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardSettingsForUnsubscribedAdminWithDisabledConsultation = new Keyboard(
//            Arrays.asList(
//                Collections.singletonList(subscribe),
//                changeStatusPlusSupport,
//                Collections.singletonList(enableConsultation),
//                Collections.singletonList(makeNews),
//                Collections.singletonList(back)
//            ));
//    private static final Keyboard keyboardSettingsForUnsubscribedAdminWithEnabledConsultation = new Keyboard(
//            Arrays.asList(
//                Collections.singletonList(subscribe),
//                changeStatusPlusSupport,
//                Collections.singletonList(disableConsultation),
//                Collections.singletonList(makeNews),
//                Collections.singletonList(back)
//            ));
//    private static final Keyboard keyboardSettingsForSubscribedUser = new Keyboard(Arrays.asList(
//            Collections.singletonList(unSubscribe),
//            changeStatusPlusSupport,
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardSettingsForSubscribedAdminWithDisabledConsultation = new Keyboard(
//            Arrays.asList(
//                    Collections.singletonList(unSubscribe),
//                    changeStatusPlusSupport,
//                    Collections.singletonList(enableConsultation),
//                    Collections.singletonList(makeNews),
//                    Collections.singletonList(back)
//            ));
//    private static final Keyboard keyboardSettingsForSubscribedAdminWithEnabledConsultation = new Keyboard(
//            Arrays.asList(
//                    Collections.singletonList(unSubscribe),
//                    changeStatusPlusSupport,
//                    Collections.singletonList(disableConsultation),
//                    Collections.singletonList(makeNews),
//                    Collections.singletonList(back)
//            ));
//    private static final Keyboard keyboardSenderForAdmin = new Keyboard(Arrays.asList(
//            Collections.singletonList(publish),
//            Collections.singletonList(cancel)
//    ));
//    private static final Keyboard keyboardSchedule = new Keyboard(Arrays.asList(
//            Collections.singletonList(lastGroup),
//            Collections.singletonList(findTeacher),
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardDialogueWithCom = new Keyboard(List.of(
//            Collections.singletonList(finishDialogue)
//    ));
//    private static final Keyboard keyboardTeachSearch = new Keyboard(Arrays.asList(
//            Collections.singletonList(findTeacherSchedule),
//            Collections.singletonList(findTeacherCard),
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardSettingsForApplicant = new Keyboard(List.of(
//            Collections.singletonList(iAmStudent),
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardStatusSelectForStudent = new Keyboard(List.of(
//            Collections.singletonList(iAmApplicant),
//            Collections.singletonList(back)
//    ));
//    private static final Keyboard keyboardConsultation = new Keyboard(List.of(
//            VIESHplusVSHSiT,
//            VSHPMplusVSHBI,
//            VSHAUplusBack
//    ));
//    private static final Keyboard keyboardFAQForApplicant = new Keyboard(List.of(
//            Collections.singletonList(tipsForApplicants),
//            undergraduatePlusMaster,
//            Collections.singletonList(contacts),
//            Collections.singletonList(back)
//    ));
//    //-----------------------------------------------------------------------------------------------------
//    public static Keyboard setKeyboard(String name){
//        return switch (name) {
//            case "Empty" -> keyboardEmpty;
//            case "Start" -> keyboardStart;
//            case "Main" -> keyboardMain;
//            case "MainForApplicantWDC" -> keyboardMainForApplicantWithDisabledConsultation;
//            case "MainForApplicantWEC" -> keyboardMainForApplicantWithEnabledConsultation;
//            case "FAQForStudent" -> keyboardFAQForStudent;
//            case "Schedule" -> keyboardSchedule;
//            case "SettingsForUnsubscribedAdminWDC" -> keyboardSettingsForUnsubscribedAdminWithDisabledConsultation;
//            case "SettingsForSubscribedAdminWDC" -> keyboardSettingsForSubscribedAdminWithDisabledConsultation;
//            case "SettingsForUnsubscribedAdminWEC" -> keyboardSettingsForUnsubscribedAdminWithEnabledConsultation;
//            case "SettingsForSubscribedAdminWEC" -> keyboardSettingsForSubscribedAdminWithEnabledConsultation;
//            case "SettingsForUnsubscribedUser" -> keyboardSettingsForUnsubscribedUser;
//            case "SettingsForSubscribedUser" -> keyboardSettingsForSubscribedUser;
//            case "SenderForAdmin" -> keyboardSenderForAdmin;
//            case "Back" -> keyboardBack;
//            case "DialogueWithCommunity" -> keyboardDialogueWithCom;
//            case "TeacherSearch" -> keyboardTeachSearch;
//            case "StatusSelection" -> keyboardStatus;
//            case "Consultation" -> keyboardConsultation;
//            case "FAQForApplicant" -> keyboardFAQForApplicant;
//            case "SettingsForApplicant" -> keyboardSettingsForApplicant;
//            case "StatusSelectForStudent" -> keyboardStatusSelectForStudent;
//            default -> keyboard;
//        };
//    }

}