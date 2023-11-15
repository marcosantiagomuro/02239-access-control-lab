package com.datasec.server;

import com.datasec.database.DatabaseConfig;
import com.datasec.database.PermissionUser;
import com.datasec.utils.enums.CommandsActionEnum;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Optional;

import static com.datasec.database.User.USERID;

public class UserAcessControl {

    public static boolean hasUserPermission(String userId, CommandsActionEnum command) throws Exception {

        ConnectionSource connectionSource = DatabaseConfig.getConnectionSource();
        Dao<PermissionUser, String> permissionUserDao = DaoManager.createDao(connectionSource, PermissionUser.class);

        try {

            QueryBuilder<PermissionUser, String> queryBuilder = permissionUserDao.queryBuilder();

            Where<PermissionUser, String> where = queryBuilder.where();
            where.eq(USERID, userId);
            where.and();
            where.eq(command.toString(), true);

            // Execute the query
            PermissionUser permissionUser = queryBuilder.queryForFirst();

            if (!Optional.ofNullable(permissionUser).isPresent()) {
                return false;
            }

            return true;

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            connectionSource.close();
        }

    }
}
