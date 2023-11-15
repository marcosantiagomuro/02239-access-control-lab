package com.datasec.server;

import com.datasec.database.DatabaseConfig;
import com.datasec.database.PermissionUser;
import com.datasec.database.Role;
import com.datasec.database.User;
import com.datasec.utils.enums.CommandsActionEnum;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.Where;
import java.sql.SQLException;
import java.util.Optional;

import static com.datasec.database.Role.ROLENAME;
import static com.datasec.database.User.USERID;

public class UserAccessControl {

    public static boolean hasUserPermission(String userId, String method, CommandsActionEnum command) throws Exception {

        ConnectionSource connectionSource = DatabaseConfig.getConnectionSource();

        switch (method) {
            case "acl":
                Dao<PermissionUser, String> permissionUserDao = DaoManager.createDao(connectionSource,
                        PermissionUser.class);

                try {
                    QueryBuilder<PermissionUser, String> queryBuilder = permissionUserDao.queryBuilder();

                    Where<PermissionUser, String> where = queryBuilder.where();
                    where.eq(USERID, userId);
                    where.and();
                    where.eq(command.toString(), true);

                    // Execute the query
                    PermissionUser permissionUser = queryBuilder.queryForFirst();

                    return Optional.ofNullable(permissionUser).isPresent();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    connectionSource.close();
                }

            case "rbac":
                Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);

                User user = userDao.queryForFirst(userDao.queryBuilder()
                        .where()
                        .eq(USERID, userId)
                        .prepare());

                Dao<Role, String> roleDao = DaoManager.createDao(connectionSource, Role.class);

                try {
                    QueryBuilder<Role, String> queryBuilder = roleDao.queryBuilder();

                    Where<Role, String> where = queryBuilder.where();
                    where.eq(ROLENAME, user.getRoleName());
                    where.and();
                    where.eq(command.toString(), true);

                    // Execute the query
                    Role role = queryBuilder.queryForFirst();

                    return Optional.ofNullable(role).isPresent();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    connectionSource.close();
                }

            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
    }
}
