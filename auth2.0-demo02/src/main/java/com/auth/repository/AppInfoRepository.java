package com.auth.repository;
import com.auth.entity.AppInfo;
import com.auth.repository.base.BaseRepository;

public interface AppInfoRepository extends BaseRepository<AppInfo> {
	AppInfo findAppInfoByAppId(String appId);
}