
local urlStr = ngx.var.document_uri

ngx.log(ngx.INFO, ">>>Request URL: "..urlStr.." <<<")

urlStr = string.sub(urlStr, 2)

local i = string.find(urlStr, [[/]])

-- get url params
local args = ngx.req.get_uri_args()

-- check image resize param
if( args ) then
    if( args['w'] and ngx.var.width) then
        ngx.var.width = tostring(args['w'])
    end
    if( args['h'] and ngx.var.height) then
        ngx.var.height = tostring(args['h'])
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

if i then
    urlStr=string.sub(urlStr, 1,i-1)
end

ngx.log(ngx.INFO, ">>>Request forward to upstream: ["..urlStr.."]  <<<")

return urlStr
