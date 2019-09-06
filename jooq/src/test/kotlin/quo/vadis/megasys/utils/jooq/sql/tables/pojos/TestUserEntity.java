/*
 * This file is generated by jOOQ.
 */
package quo.vadis.megasys.utils.jooq.sql.tables.pojos;


import org.jooq.JSONB;

import quo.vadis.megasys.utils.jooq.sql.tables.interfaces.ITestUser;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestUserEntity implements ITestUser {

    private static final long serialVersionUID = 1065358654;

    private Long   userId;
    private String userName;
    private JSONB  payload;

    public TestUserEntity() {}

    public TestUserEntity(ITestUser value) {
        this.userId = value.getUserId();
        this.userName = value.getUserName();
        this.payload = value.getPayload();
    }

    public TestUserEntity(
        Long   userId,
        String userName,
        JSONB  payload
    ) {
        this.userId = userId;
        this.userName = userName;
        this.payload = payload;
    }

    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public JSONB getPayload() {
        return this.payload;
    }

    @Override
    public void setPayload(JSONB payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestUserEntity (");

        sb.append(userId);
        sb.append(", ").append(userName);
        sb.append(", ").append(payload);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ITestUser from) {
        setUserId(from.getUserId());
        setUserName(from.getUserName());
        setPayload(from.getPayload());
    }

    @Override
    public <E extends ITestUser> E into(E into) {
        into.from(this);
        return into;
    }
}
