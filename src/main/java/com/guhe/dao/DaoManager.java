package com.guhe.dao;

import javax.servlet.http.HttpServletRequest;

public interface DaoManager {

	Dao getDao(HttpServletRequest req);
}
