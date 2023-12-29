# "Облачное домашнее хранилище"
## Идея:
Создать приложение, которое позволит получить бесплатное хранилище, доступное через интернет. Хранилище 
ограниченно лишь размером подключенного жесткого диска. Размер загружаемых файлов ограничен возможностями яндекс диском. 

## Используемые API
* [Yandex.Disk Java SDK For REST API](https://mvnrepository.com/artifact/com.yandex.android/disk-restapi-sdk/1.03)
* [Java VK Bots Long Poll API](https://github.com/yvasyliev/java-vk-bots-long-poll-api)

## Cтруктура:
Приложение использует объектно-ориентированную парадигму программирования и содержит набор классов:
* class Bot - класс Вк бота, который отвечает на сообщения
* class MyKeyboard - класс для создания клавиатур
* class Daemon - демон поток, который постоянно проверяет наличие новых файлов на яндекс диске
* class Supervisor - поток, который отправляет сообщение пользователю после того как сработал Daemon
* class JsonWorker - для того, чтобы подгрузить файл keyboard.json (для инициализации клавиатуры)
* class PathToImage - класс для создания картинки с деревом файлов и папок, или только папок
* class StorageController - отвечает за загрузку и удаление файлов на локальном хранилище и яндекс диске

## Демонстрация работы
### Загрузка Яндекс диск -> Лок. хранилище
При загрузке новых файлов на яндекс диск, бот сообщит об их появлении и предложит сохранить файлы, или отменить их сохранение<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image0.jpg)
<br/>
После нажатия на кнопку "Сохранить" произойдет следующее: 
* файлы скачаются с яндекс диска по указанному пути (location_to_sync в [config.properties](https://github.com/A192747/Cloud_home_storage/blob/main/config.properties.origin))
* затем файлы удалятся с яндекс диска, чтобы не занимать место. При этом файлы могут быть помещены в корзину, если у пользователя включена данная функция<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image1.jpg)
<br/>


### Кнопка "Выбрать файлы"
При её нажатии пользователь получает изображение с деревом папок и файлов в папке (с сохраненными в локальном хранилище файлами (location_to_sync в [config.properties](https://github.com/A192747/Cloud_home_storage/blob/main/config.properties.origin)))<br/> 
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image3.jpg)
<br/>
Если фото с деревом слишком большое, то фото отправлятеся в виде файла. Но если текст не умещается на фото, то пользователю не отправляется фото =(<br/>
<br/>
Далее пользователь вводит необходимые ему названия файлов через запятую (к примеру: "Горы, Зима, Море"), либо названия папок<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image4.jpg)
<br/>
Затем пользоваель выбирает подходящие ему варианты (вводя ответ в формате "1, 2, 3, 4" или написав "Все")<br/>
Пользователь может решить что дальше сделать с файлами. Загрузить на яндекс диск, или удалить из локального хранилища<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image5.jpg)


### Кнопка "Выбрать папку для сохранения на пк"
При её нажатии пользователь получает изображение с деревом папок в папке для сохранения файлов (location_to_sync в [config.properties](https://github.com/A192747/Cloud_home_storage/blob/main/config.properties.origin))<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image6.jpg)
<br/>
Если фото с деревом большое, то фото отправлятеся в виде файла. Но если текст не умещается на фото, то пользователю не отправляется фото =(<br/>
<br/> Далее пользователь выбирает нужную директорию, и она становится стандартной для загрузки (на оди раз)<br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image7.jpg)


### Кнопка "Инфо"
При нажатии в главном меню кнопки "Инфо" пользователь получает информацию о яндекс диске и перечень действующих настроек. <br/>
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image2.jpg)
<br/>
Причем если во время этапа "Загрузка Яндекс диск -> Лок. хранилище" пользователь выбрал "Отмена", то при нажатии на "Инфо" заново появится сообщение с возможностью Сохранить/Отмена файлы с яндекс диска 

### Кнопки в настройках
![Screenshot](https://github.com/A192747/Cloud_home_storage/blob/main/images/image8.jpg)
<br/>

## Быстрый старт
Необходимо заполнить (создать) файл config.properties с полями из [config.properties.origin](https://github.com/A192747/Cloud_home_storage/blob/main/config.properties.origin).<br/>
Формат ввода полей необходимо соблюдать такой-же.

*yandex_token* - можно получить благодаря инструкции с [этого](https://yandex.ru/dev/disk/api/concepts/quickstart.html) сайта <br/>
*vk_token* - можно получить при создании группы в вк<br/>
*user_vk_id* - укажите ваш id пользователя в вк (обязательно цифрами)<br/>
*location_to_sync* - укажите путь, куда будут сохраняться файлы на локальном хранилище (с "\\" в конце)<br/>
*location_keyboard_json* - укажите путь до файла keyboard.json (если вы решили его переместить)<br/>
*update_delay* - укажите задержку на обновление демона, который проверяет наличие новых файлов на яндекс диске. (советую оставить 5000)<br/>



