-- 切割为了获取 /XXX/YYYY 中的 XXX，根据 XXX 去查找配置 upstream
local urlStr = ngx.var.document_uri

ngx.log(ngx.INFO, ">>>Request URL1: "..urlStr.." <<<")

-- 去掉 /XXX/YYYY 前面一个 /，得到  XXX/YYYY
urlStr = string.sub(urlStr, 2)

-- 获取  XXX/YYYY 中 / 的位置
local i = string.find(urlStr, [[/]])

-- 获取 url 参数
local args = ngx.req.get_uri_args()

-- 检查是否带有 image 尺寸参数
if( args ) then
    if( args['w'] and ngx.var.width) then
        ngx.var.width = tostring(args['w'])
        ngx.log(ngx.INFO, "width....: "..ngx.var.width.."  <<<")
    end
    if( args['h'] and ngx.var.height) then
        ngx.var.height = tostring(args['h'])
        ngx.log(ngx.INFO, "height....: "..ngx.var.height.."  <<<")
    end
end

-- check if has limited header
local limitedHeader = ngx.var.limitedHeaderName
if limitedHeader then
    -- check header
    if( ngx.var.headerValue ) then
        local headers = ngx.req.get_headers()
        local header = tostring(headers[limitedHeader])
        if( header == ngx.var.headerValue) then
            ngx.log(ngx.INFO, "HEADER ["..limitedHeader.."] value matched     ....: "..header.."  <<<")
        else
            ngx.log(ngx.INFO, "HEADER ["..limitedHeader.."] value not matched, client value is ....: "..header.."  <<<")
            ngx.var.rCode = 403
        end
    else
        ngx.log(ngx.INFO, "please set >>headerValue<< param on location")
    end
end

-- 如果 i 有值，则切割 XXX/YYYY 得到最终值 XXX
if i then
urlStr=string.sub(urlStr, 1,i-1)
end

ngx.log(ngx.INFO, ">>>Request forward to upstream: ["..urlStr.."]  <<<")

-- 返回值 XXX
return urlStr






