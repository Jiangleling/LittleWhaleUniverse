package com.example.littlewhaleuniverse;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


// @Entity注解，tableName指定了数据库中表的名字。
@Entity(tableName = "diaries")

public class DiaryEntry {

    //@PrimaryKey 注解将id字段设置为主键
    //autoGenerate = true 表示id将由数据库自动生成

    @PrimaryKey(autoGenerate = true)
    public int id;

    // @ColumnInfo注解可以为表中的列指定一个不同的名字。
    // 如果不指定，则默认使用字段名。
    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // 构造函数，用于创建DiaryEntry对象
    public DiaryEntry(String title,String content, long timestamp){
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // --- 下面是标准的Getters和Setters ---
    // Room需要这些方法来访问字段
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
