package com.solar.growatt;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final Handler ui = new Handler(Looper.getMainLooper());
    private TextView state, log;
    private Button check;
    private final StringBuilder history = new StringBuilder();
    private static final String HOST = "server-au.growatt.com";
    private static final String LOCAL = "192.168.0.141";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 32, 24, 24);
        layout.setBackgroundColor(Color.rgb(245,248,251));
        scroll.addView(layout);
        heading(layout,"☀ Growatt Solar Monitor",26);
        label(layout,"SPF 5000 ES  •  AXE 5.0L-C1 × 3  •  15 кВт·ч",15);
        heading(layout,"Показатели станции",20);
        label(layout,"Солнечная генерация: — кВт",18);
        label(layout,"Нагрузка дома: — Вт",18);
        label(layout,"Заряд аккумуляторов: — %",18);
        label(layout,"Выработка за день: — кВт·ч",18);
        label(layout,"Реальные данные ещё не подключены. Прочерки — не нулевые показания.",13);
        heading(layout,"Диагностика связи",20);
        label(layout,"Логгер: DDD0CJC1N6",15);
        label(layout,"Локальный IP: " + LOCAL,15);
        label(layout,"Облачный сервер: " + HOST + ":5279",15);
        state = label(layout,"Нажмите «Проверить связь»",16);
        check = new Button(this); check.setText("Проверить связь"); layout.addView(check);
        check.setOnClickListener(v -> runCheck());
        Button open = new Button(this); open.setText("Открыть страницу логгера"); layout.addView(open);
        open.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + LOCAL))));
        Button share = new Button(this); share.setText("Поделиться журналом"); layout.addView(share);
        share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_TEXT, history.toString());
            startActivity(Intent.createChooser(intent,"Отправить журнал"));
        });
        heading(layout,"Журнал проверок",20);
        log = label(layout,"Пока пусто",14);
        setContentView(scroll);
    }
    private TextView label(LinearLayout root,String s,int size) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.rgb(30,45,58));
        t.setPadding(0,9,0,9); root.addView(t); return t;
    }
    private void heading(LinearLayout root,String s,int size) {
        TextView t = label(root,s,size); t.setTypeface(null,Typeface.BOLD);
    }
    private String testPort(String host,int port,int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host,port),timeout);
            return "TCP " + host + ":" + port + " — доступен";
        } catch (Exception e) {
            return "TCP " + host + ":" + port + " — нет соединения (" + e.getClass().getSimpleName() + ")";
        }
    }
    private void runCheck() {
        check.setEnabled(false); state.setText("Проверка выполняется…");
        worker.execute(() -> {
            StringBuilder result = new StringBuilder();
            result.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(new Date())).append("\n");
            result.append(testPort(LOCAL,80,3500)).append("\n");
            try {
                InetAddress[] addresses = InetAddress.getAllByName(HOST);
                result.append("DNS ").append(HOST).append(" — ").append(addresses.length > 0 ? addresses[0].getHostAddress() : "нет адреса").append("\n");
                result.append(testPort(HOST,5279,4500)).append("\n");
            } catch (Exception e) { result.append("DNS — ошибка: ").append(e.getClass().getSimpleName()).append("\n"); }
            result.append("TCP-доступность не подтверждает авторизацию или передачу телеметрии.\n\n");
            ui.post(() -> { history.insert(0,result.toString()); log.setText(history.toString()); state.setText("Проверка завершена"); check.setEnabled(true); });
        });
    }
    @Override protected void onDestroy() { worker.shutdownNow(); super.onDestroy(); }
}
