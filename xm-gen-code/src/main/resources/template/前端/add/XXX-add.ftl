<!DOCTYPE html>
<html>
<head>
 <title>${t.tableComment}-添加/修改</title>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
 <style type="text/css">
  .c-panel .el-form .c-label{width: 12em !important;}
  .c-panel .el-form .full-size .el-input, .c-panel .el-form .full-size .el-textarea__inner {width: 550px;}
 </style>
</head>
<body>
<div class="vue-box" :class="{sbot: id}" style="display: none;" :style="'display: block;'">
 <!-- ------- 内容部分 ${kv.requestPath} ------- -->
 <div class="s-body">
  <div class="c-panel">
   <div class="c-title" v-if="id == 0">添加${t.tableComment}</div>
   <div class="c-title" v-else>修改${t.tableComment}</div>
   <el-form v-if="m">
    <#list cs as c>
      <sa-item type="text" name="${c.columnComment}" v-model.trim="m.${c.javaField}" br></sa-item>
    </#list>
    <sa-item name="" class="s-ok" br>
     <el-button type="primary" icon="el-icon-plus" @click="ok()">保存</el-button>
    </sa-item>
   </el-form>
  </div>
 </div>
 <!-- ------- 底部按钮 ------- -->
 <div class="s-foot">
  <el-button type="primary" @click="ok()">确定</el-button>
  <el-button @click="sa.closeCurrIframe()">取消</el-button>
 </div>
</div>
<script>
			var app = new Vue({
				el: '.vue-box',
				data: {
					id: sa.p('id', 0),		// 获取超链接中的id参数（0=添加，非0=修改）
					m: null,		// 实体对象
				},
				mounted: function(){
					// 初始化数据
					if(this.id <= 0) {
						this.m = this.createModel();
					} else {
						sa.ajax(`${r'${API_URL_PREFIX}'}/getById?id=` + this.id, function(res) {
							this.m = res.data;
							if(res.data == null) {
								sa.alert('未能查找到 id=' + this.id + " 详细数据");
							}
						}.bind(this))
					}
				},
				methods: {
					// 创建一个 默认Model
					createModel: function() {
						return {
<#list cs as c>
                             ${c.javaField}: '',		// ${c.columnComment}
</#list>
						}
					},
					// 提交数据
					ok: function(){
						// 表单校验
						let m = this.m;
<#list cs as c>
						sa.checkNull(m.${c.javaField}, '请输入 [${c.columnComment}]');
 </#list>

						if(this.id <= 0) {	// 添加
							sa.ajax(`${r'${API_URL_PREFIX}'}/add`, sa.removeNull(m), function(res){
								sa.alert('增加成功', this.clean);
							}.bind(this), {contentType: 'json'});
						} else {	// 修改
							sa.ajax(`${r'${API_URL_PREFIX}'}/update`, m, function(res){
								sa.alert('修改成功', this.clean);
							}.bind(this), {contentType: 'json'});
						}
					},
					// 添加/修改 完成后的动作
					clean: function() {
						if(this.id == 0) {
							this.m = this.createModel();
						} else {
							parent.app.f5();		// 刷新父页面列表
							sa.closeCurrIframe();	// 关闭本页
						}
					}
				},
				components: {
					"sa-item": httpVueLoader('../../sa-frame/com/sa-item.vue')
				}
			})

		</script>
</body>
</html>