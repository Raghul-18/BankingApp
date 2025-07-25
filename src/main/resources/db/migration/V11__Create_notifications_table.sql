CREATE TABLE notifications (
    notification_id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id NUMBER,
    message VARCHAR2(500),
    sent_date DATE DEFAULT SYSDATE,
    is_read CHAR(1) DEFAULT 'N',
    CONSTRAINT fk_notify_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);