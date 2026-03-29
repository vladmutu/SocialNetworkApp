CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profilepicture BYTEA NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users (email);

CREATE TABLE IF NOT EXISTS friendships (
    id BIGSERIAL PRIMARY KEY,
    user1 BIGINT NOT NULL,
    user2 BIGINT NOT NULL,
    friendssince TIMESTAMP,
    pending BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_friendships_user1 FOREIGN KEY (user1) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_user2 FOREIGN KEY (user2) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_friendships_distinct_users CHECK (user1 <> user2)
);

CREATE INDEX IF NOT EXISTS ix_friendships_user1 ON friendships (user1);
CREATE INDEX IF NOT EXISTS ix_friendships_user2 ON friendships (user2);

CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    senderid BIGINT NOT NULL,
    receiverid BIGINT NOT NULL,
    date TIMESTAMP,
    message TEXT NOT NULL,
    reply_id BIGINT,
    CONSTRAINT fk_messages_sender FOREIGN KEY (senderid) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_receiver FOREIGN KEY (receiverid) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_reply FOREIGN KEY (reply_id) REFERENCES messages (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS ix_messages_sender_receiver_date ON messages (senderid, receiverid, date);

