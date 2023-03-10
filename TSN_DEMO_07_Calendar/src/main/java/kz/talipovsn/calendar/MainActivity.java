package kz.talipovsn.calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final int maxSemesterWeek = 15; // Максимальное количество недель в семестре

    final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, EEEE"); // Формат для даты
    final Calendar calendar = Calendar.getInstance(); // Класс Java для работы с календарем

    SharedPreferences sp; // Переменная для работы с настройками приложения
    CalendarView cal;  // Переменная для доступа к компоненту календаря
    EditText editText; // Переменная для доступа к текстовому компоненту вывода

    String dateOfBirth = "dateOfBirth";
    String fullName = "fullName";

    boolean offsetNumerator; // Логическая поправка к расчету
    int displacementWeekSemester; // Номер недели начала семестра

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация доступа к настройкам программы
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Получение доступа к текстовому компоненту "editText"
        editText = findViewById(R.id.editText);

        // Доступ к компоненту календаря
        cal = findViewById(R.id.calendarView);
        cal.setFirstDayOfWeek(2); // Установка первым днем недели понедельника в календаре Java

        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Установка первым днем недели понедельника в компоненте Android

        readSettings(); // Чтение и инициализация настроек программы

        // Обработчик выбора даты в календаре
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                editText.setText(doCalc());
            }
        });

    }

    // Чтение настроек и инициализация параметров
    private void readSettings() {
        // Чтение сохраненных настроек расчета
        offsetNumerator = sp.getBoolean("offsetNumerator", false);
        displacementWeekSemester = Integer.valueOf(sp.getString("displacementWeekSemester", "1")) - 1;

        dateOfBirth = sp.getString("dateOfBirth","");
        fullName = sp.getString("fullName","");

        System.out.println(dateOfBirth);
        System.out.println(fullName);

        // Установка нужного размера шрифта для компонента "editText" из настроек
        int fontSize = Integer.valueOf(sp.getString("fontSize", String.valueOf((getString(R.string.fontSize)))));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    // Открытие окна при его создании и переворотах экрана
    @Override
    protected void onResume() {
        super.onResume();
        readSettings();
        Date date = new Date();
        cal.setDate(date.getTime());
        calendar.setTime(date);
        editText.setText(doCalc());
    }

    // Функция определения четности
    public static boolean isEven(int x) {
        return (x & 1) == 0;
    }

    // Расчет номера учебной недели и определение числительная она или знаменательная
    public String doCalc() {
        String date = sdf.format(calendar.getTime());
        int semesterWeek = calendar.get(Calendar.WEEK_OF_YEAR) - displacementWeekSemester;
        boolean numerator = isEven(semesterWeek);



        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date1 = myFormat.parse(dateOfBirth);
            Date date2 = myFormat.parse(date);
            long diff = date2.getTime() - date1.getTime();

            int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            int pOne = 23;
            int pTwo = 28;
            int pThree = 33;

            double bPhysical = (Math.sin(2 * Math.PI * days / pOne)) * 100;

            double bEmotional = (Math.sin(2 * Math.PI * days / pTwo)) * 100;

            double bIntellectual = (Math.sin(2 * Math.PI * days / pThree)) * 100;

            System.out.println(bPhysical);
            System.out.println(bPhysical);
            System.out.println(bPhysical);
            System.out.println("Прошло " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " дней с " + date1 + " до " + date2);
            final String finalDate = "Прошло " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " дней";
            return fullName + "\n" + finalDate + "\n" + semesterWeek + " неделя года" + "\n" + "Физ.: " +
                    Double.valueOf(Math.round(bPhysical)) + ", Эмоц.: " + Double.valueOf(Math.round(bEmotional)) + ", Интеллект.: " + Double.valueOf(Math.round(bIntellectual));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fullName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Обработчик меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.about) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.email, Toast.LENGTH_LONG);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
