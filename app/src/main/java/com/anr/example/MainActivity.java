package com.anr.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MainActivity extends AppCompatActivity {

    Button simpleDeadlock;
    Button readWriteDeadLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        simpleDeadlock = (Button) findViewById(R.id.simple_deadlock_button);
        simpleDeadlock.setOnClickListener(new View.OnClickListener() {
            Object lock1 = new Object();
            Object lock2 = new Object();

            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 100; i++) {
                            // Take a look into lock ordering and don't repeat it at home
                            synchronized (lock2) {
                                synchronized (lock1) {
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }.start();

                for (int i = 0; i < 100; i++) {
                    // lock ordering is different from previous place
                    synchronized (lock1) {
                        synchronized (lock2) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });

        readWriteDeadLock = (Button) findViewById(R.id.read_write_dead_lock);
        readWriteDeadLock.setOnClickListener(new View.OnClickListener() {

            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            private void write() {
                try {
                    lock.writeLock().lock();
                }finally {
                    lock.writeLock().unlock();
                }
            }

            @Override
            public void onClick(View v) {


                try {
                    lock.readLock().lock();
                    write();
                } finally {
                    lock.readLock().unlock();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
