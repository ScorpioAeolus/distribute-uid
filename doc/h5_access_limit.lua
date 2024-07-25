local cjson=require 'cjson'
local geo=require 'resty.maxminddb'
local redis_host    = "lottery-redis.nyqdkg.ng.0001.apse1.cache.amazonaws.com"
local redis_port    = 6379

local redis_connection_timeout = 100
local redis_key     = "option_iplimit_ignore_ip_key"
local redis_black_key     = "option_iplimit_black_ip_key"
local cache_ttl     = 1
--local ip                = ngx.var.arg_ip
local ip = ngx.var.remote_addr
local ip_whitelist              = ngx.shared.ip_whitelist
local ip_blacklist              = ngx.shared.ip_blacklist
local last_update_time  = ip_whitelist:get("last_update_time");

local inWhiteList = false

if last_update_time == nil or last_update_time < ( ngx.now() - cache_ttl ) then
  local redis = require "resty.redis";
  local red = redis:new();
  red:set_timeout(redis_connect_timeout);
  local ok, err = red:connect(redis_host, redis_port);
  if not ok then
          ngx.log(ngx.INFO, "Redis connection error while retrieving ip_whitelist: " .. err);
  else
        local new_ip_whitelist, err = red:smembers(redis_key);
        if err then
                ngx.log(ngx.ERR, "Redis read error while retrieving ip_whitelist: " .. err);
        else
      
                ip_whitelist:flush_all();
        for index, banned_ip in ipairs(new_ip_whitelist) do
                ip_whitelist:set(banned_ip, true);
         end
                ip_whitelist:set("last_update_time", ngx.now());
        end
        local new_ip_blacklist, err = red:smembers(redis_black_key);
        if err then
                ngx.log(ngx.ERR, "Redis read error while retrieving ip_blacklist: " .. err);
        else

                ip_blacklist:flush_all();
        for index, black_ip in ipairs(new_ip_blacklist) do
                ip_blacklist:set(black_ip, true);
         end
                ip_blacklist:set("last_update_time", ngx.now());
        end
  end
end

--判断ip是否在白名单中,不在则直接拒绝处理
if (ip_whitelist:get("\""..ip.."\"")) then
        -- 如果在白名单,则直接返回通过
        ngx.log(ngx.ERR,"ip:"..ip..",is in whitelist,return success")
        return
elseif (ip_blacklist:get("\""..ip.."\""))  then
        ngx.log(ngx.ERR,"ip:"..ip..",is in blacklist,return fail")
        ngx.exec("@fail_internal")
else
   local isPh = true
   local country_code = ""
   local isCountryCn = true
   local isRegisterCountryCn = true
   local isRepresentCountryCn = true
   if not geo.initted() then
        geo.init("/opt/tools/geoIp2/GeoLite2-Country_20240322/GeoLite2-Country.mmdb")
   end
   local res,err=geo.lookup(ip)
   if not res then
       -- ngx.say("获取客户端ip失败,或当前请求的ip不是公网ip")
        ngx.log(ngx.ERR,' failed to lookup by ip , reason :',err)
   else
        for k,v in pairs(res) do
                 if(k == "country") then
                        for key,item in pairs(v) do
                                if (key=="iso_code") then
                                       country_code = item
                                       if (item == "CN" or item == "HK" or item == "MO" or item == "TW") then
                                                isCountryCn=true
                                       else
                                                isCountryCn=false
                                       end
                                end
                        end
                 end
               if(k == "registered_country") then
                        for key,item in pairs(v) do
                                if (key=="iso_code") then
                                       country_code = item
                                       if (item == "CN" or item == "HK" or item == "MO" or item == "TW") then
                                                isRegisterCountryCn=true
                                       else
                                                isRegisterCountryCn=false
                                       end
                                end
                        end
                end
               if( k == "represented_country") then
                        for key,item in pairs(v) do
                                if (key=="iso_code") then
                                       country_code = item
                                       if (item == "CN" or item == "HK" or item == "MO" or item == "TW") then
                                                isRepresentCountryCn=true
                                       else
                                                isRepresentCountryCn=false
                                       end
                                end
                        end
                end

        end
   end
   if isCountryCn and isRegisterCountryCn and isRepresentCountryCn then
        isPh = false
   end 
   if isPh==true then
       return
   else
        local http = require('resty.http')
        local httpc = http.new()
        local res, err = httpc:request_uri("https://ipinfo.io", {  
                method = "GET",  
                path = "/"..ip.."?token=2ff8beaac2e34b",  
                headers = {  
                 ["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36"  
                },
                ssl_verify = false,
                keepalive_timeout = 2000
        })
        if not res then
                ngx.log(ngx.ERR, "request error#"..tostring(res))
                return
        end
        local status = res.status
        --ngx.log(ngx.ERR, "Java service response status: "..status..",body="..res.body)  
        if status == 200 then
                local result = cjson.decode(res.body)
                local country = result.country
                 ngx.log(ngx.ERR,"ip="..ip..",country=",country)
                if ("CN" == country or "HK" == country or "MO" == country or "TW" == country ) then
                        ngx.log(ngx.ERR, "ipinfo checked ip not allow;ip="..ip..",country="..country)
                        ngx.exec("@fail_internal")
                else
                       return
                end
        else
                return
        end
   end
end
