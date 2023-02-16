<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <link rel="icon" href="data:;base64,=">
    <script src="/vue.js"></script>
    <script src="/axios.min.js"></script>
    <script src="/request.js"></script>
</head>
<body>
<div>
    <h1><a href="/department/view/index">部门列表页</a></h1>
    <h1>${title}</h1>
    <h1><a href="/dict/view/tree">字典树页</a></h1>
</div>
<div id="app">
    <div>
        <input type="button" value="刷新列表" @click="refresh">
        <input v-show="!isShowAddForm &&!isShowModify&&!isChangeSort" type="button" value="新增" @click="showAddForm">
        <input v-show="!isShowAddForm &&!isShowModify&&!isChangeSort" type="button" value="下载导入模板"
               @click="downloadTemplate">
        <input v-show="!isShowAddForm &&!isShowModify&&!isChangeSort" type="button" value="导入数据"
               @click="showImport">
        <input v-show="isShowAddForm ||isShowModify||isChangeSort" type="button" value="关闭" @click="hideAll">
    </div>

    <div v-show="isShowAddForm">
        <div>
            <span>上级字典:</span>
            <select v-model="form.parentId">
                <option value="-1">请选择</option>
                <option v-for="item in list" :value="item.id">{{item.dicDescription}}</option>
            </select>
        </div>
        <div>
            <span>字典名:</span>
            <input type="text" v-model="form.dicDescription"/>
        </div>
        <div>
            <span>字典编码:</span>
            <input type="text" v-model="form.dicCode"/>
        </div>
        <div>
            <span>字典值:</span>
            <input type="text" v-model="form.dicValue"/>
        </div>
        <div>
            <input type="button" value="提交" @click="save"/>
        </div>
    </div>


    <div v-if="isShowModify">
        <div>
            <span>上级字典:</span>
            <select v-model="updateForm.parentId">
                <option value="-1">请选择</option>
                <option v-for="item in list" :value="item.id">{{item.dicDescription}}</option>
            </select>
        </div>
        <div>
            <span>字典名:</span>
            <input type="text" v-model="updateForm.dicDescription"/>
        </div>
        <div>
            <span>字典编码:</span>
            <input type="text" v-model="updateForm.dicCode"/>
        </div>
        <div>
            <span>字典值:</span>
            <input type="text" v-model="updateForm.dicValue"/>
        </div>
        <div>
            <input type="button" value="提交" @click="update"/>
        </div>
    </div>

    <div style="height: 20px;">
        <div>{{successMsg}}</div>
        <div>{{errorMsg}}</div>
    </div>

    <div v-if="isChangeSort">
        <span>交换对象:</span>
        <select v-model="targetId" :value="targetId" @change="changeSortConfirm">
            <option value="-1">请选择</option>
            <option v-for="item in changeList" :value="item.id">{{item.dicDescription}}</option>
        </select>
    </div>

    <div>
        <div v-if="list.length>0">
            <table border="1">
                <tr>
                    <th>字典名</th>
                    <th>字典编码</th>
                    <th>字典值</th>
                    <th>序号</th>
                    <th>操作</th>
                </tr>
                <tr v-for="item in list">
                    <td>{{item.dicDescription}}</td>
                    <td>{{item.dicCode}}</td>
                    <td>{{item.dicValue}}</td>
                    <td>{{item.sort}}</td>
                    <td>
                        <input type="button" value="删除" @click="del(item);">
                        <span>&nbsp;&nbsp;</span>
                        <input type="button" value="置顶" @click="upTop(item);">
                        <span>&nbsp;&nbsp;</span>
                        <input type="button" value="上移" @click="upMove(item);">
                        <span>&nbsp;&nbsp;</span>
                        <input type="button" value="下移" @click="downMove(item);">
                        <span>&nbsp;&nbsp;</span>
                        <input type="button" value="交换" @click="changeSort(item);">
                        <span>&nbsp;&nbsp;</span>
                        <input type="button" value="修改" @click="showModify(item);">
                    </td>
                </tr>
            </table>
            <div>
                <span>分页数量</span>
                <input type="text"  v-model="page.pageSize">
            </div>
            <div>
                <input type="button" v-if="page.pageNo>1" value="上一页" @click="toPage(page.pageNo-1);">
                <span>第</span>
                <input type="button" :value="page.pageNo>0?page.pageNo:1" @click="toPage(1);">
                <input type="button" :value="page.pageNo>1?page.pageNo+1:2" @click="toPage(2);">
                <input type="button" :value="page.pageNo>1?page.pageNo+2:3" @click="toPage(3);">
                <input type="button" :value="page.pageNo>1?page.pageNo+3:4" @click="toPage(4);">
                <input type="button" :value="page.pageNo>1?page.pageNo+4:5" @click="toPage(5);">
                <span>页</span>
                <input type="button" value="下一页" @click="toPage(page.pageNo+1);">
            </div>
        </div>
        <div v-else>
            无数据
        </div>
    </div>


    <div v-if="isShowImport">
        <h2>字典导入</h2>
        <div>
            <form id="form" action="/employee_info/importEmp" method="post">
                <input type="file" @change="getImportFile($event)" multiple="multiplt"
                       class="add-file-right-input" accept=".xlsx,.xls">
                <input type="button" @click="submitImportFile" value="上传导入文件"/>
            </form>
        </div>
    </div>

</div>
</body>
<script>
    var app = new Vue({
      el: '#app',
      props:[],
      data: {
        query: {
            parentId: '',
            dicDescription: '',
            dicCode: '',
            dicValue: '',
        },
        form: {
            parentId: '-1',
            dicDescription: '',
            dicCode: '',
            dicValue: '',
        },
        updateForm: {
            id: '',
            parentId: '-1',
            dicDescription: '',
            dicCode: '',
            dicValue: '',
        },
        list: [],

        successMsg: '',
        errorMsg: '',

        isShowImport: false,
        isShowAddForm: false,
        isShowModify: false,
        isChangeSort: false,
        source: {
            parentId: '-1',
        },
        targetId: '',

        importFiles: [],

        page: {
            pageNo: 1,
            pageSize: 10
        }

      },
      watch: {
          // source(newVal, oldVal) {},
      },
      async created() {
        await this.getList()
      },
      async mounted() {
      },
      methods: {


        // 查询列表数据
        async toPage(pageNo) {
            this.page.pageNo = pageNo
            await this.getList()
        },
        // 查询列表数据
        async getList() {
            let res = await api({
                method: 'post',
                url:"/dict/list?pageNo="+ this.page.pageNo + "&pageSize=" + this.page.pageSize,
                headers: {
                  "Content-Type": "application/json",
                },
                data: this.query,
            })
            if (res) {
                // this.showMsg('查询成功!')
                let dataList = res.data
                this.list = dataList
            }
        },


        // 提交表单
        async save() {
            let res = await api({
                method: 'post',
                url:"/dict/add",
                headers: {
                  "Content-Type": "application/json",
                },
                data: this.form,
            })
            if (res) {
                this.showMsg('新增成功!')
                this.hideAll()
                await this.getList()
            }
        },

        // 更新表单
        async update() {
            let res = await api({
                method: 'post',
                url:"/dict/update",
                headers: {
                  "Content-Type": "application/json",
                },
                data: this.updateForm,
            })
            if (res) {
                this.showMsg('修改成功!')
                this.hideAll()
                await this.getList()
            }
        },

        // 显示提交表单
        showAddForm() {
           this.isShowAddForm = true
           this.isShowImport = true
           this.isShowModify = false
           this.isChangeSort = false
        },

        // 显示修改表单
        showModify(item) {
           this.isShowAddForm = false
           this.isShowImport = false
           this.isShowModify = true
           this.isChangeSort = false
           this.updateForm = JSON.parse(JSON.stringify(item))
        },

        // 显示导入
        showImport() {
           this.isShowAddForm = false
           this.isShowImport = true
           this.isShowModify = false
           this.isChangeSort = false
        },

        // 下载模板
        async downloadTemplate() {
            let res = await api.download({
                method: 'post',
                url:"/dict/downloadTemplate",
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                data: {},
            })
            if (res) {
                console.log("导出成功")
            }
        },

        // 收集上传文件
        getImportFile(event) {
            this.importFiles = []
            var file = event.target.files;
            for (var i = 0; i < file.length; i++) {
                //    上传类型判断
                var imgName = file[i].name;
                var idx = imgName.lastIndexOf(".");
                if (idx != -1) {
                    var ext = imgName.substr(idx + 1).toUpperCase();
                    ext = ext.toLowerCase();
                    this.importFiles.push(file[i]);
                } else {

                }
            }
        },

        // 提交上传文件
        async submitImportFile() {
            console.log('submitImportFile')
            if (0 == this.importFiles.length) {
                alert('请选择要上传的文件');
                return;
            }

            var formData = new FormData();
            for (var i = 0; i < this.importFiles.length; i++) {
                formData.append('file', this.importFiles[i]);
            }

            let res = await api({
                method: 'post',
                url: "/dict/import",
                data: formData,
            })
            if (res) {
                console.log("导入成功")
                this.isShowImport = false
                this.importFiles = []
                await this.getList()
            }

        },

        // 删除文件
        async del(item) {
            let res = await api({
                method: 'post',
                url:"/dict/del",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id},
            })
            if (res) {
                this.showMsg('删除成功!')
                await this.getList()
            }
        },

        // 置顶
        async upTop(item) {
            let res = await api({
                method: 'post',
                url:"/dict/upTop",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentId: item.parentId},
            })
            if (res) {
                this.showMsg('置顶成功!')
                await this.getList()
            }
        },

        // 上移一位
        async upMove(item) {
            let res = await api({
                method: 'post',
                url:"/dict/upMove",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentId: item.parentId},
            })
            if (res) {
                this.showMsg('上移成功!')
                await this.getList()
            }
        },

        // 下移一位
        async downMove(item) {
            let res = await api({
                method: 'post',
                url:"/dict/downMove",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentId: item.parentId},
            })
            if (res) {
                this.showMsg('下移成功!')
                await this.getList()
            }
        },

        // 显示交换控件
        async changeSort(item) {
            this.isShowAddForm = false
            this.isShowImport = false
            this.isChangeSort = true
            this.isShowModify = false
            this.source = item
        },

        // 提交交换两个位置
        async changeSortConfirm() {
            let res = await api({
                method: 'post',
                url:"/dict/changeSort?preId=" + this.source.id + "&nextId=" + this.targetId,
            })
            if (res) {
                this.showMsg('交换成功!')
                this.hideAll()
                await this.getList()
            }
        },

        // 显示信息
        showMsg(msg) {
            this.successMsg = msg
            this.cleanMsg()
        },

        // 清除信息
        cleanMsg() {
            setTimeout(() => {
                this.successMsg = ''
            }, 1000)
        },

        // 隐藏所有
        hideAll() {
            this.isShowAddForm = false
            this.isShowImport = false
            this.isChangeSort = false
            this.isShowModify = false
        },

        // 刷新
        async refresh() {
            this.hideAll()
            await this.getList()
        },

      },
      computed: {
          changeList() {
              if (this.source.parentId === '-1') {
                  return this.list.filter(s=>s.id!==this.source.id)
              } else {
                  const parent = this.list.find(s=>s.id === this.source.parentId)
                  if (parent.children&&parent.children.length>0) {
                      return parent.children.filter(s=>s.id!==this.source.id)
                  } else {
                      return []
                  }
              }
          }
      },
      components: {
         'treeitem': Vue.component('tree-item',{
            props: ['item'],
            data: function () {
              return {
              }
            },
            methods: {
                showModify(iitem) {
                   this.$emit('show-modify', iitem)
                },

                async del(iitem) {
                   this.$emit('del', iitem)
                },

                async upTop(iitem) {
                   this.$emit('up-top', iitem)
                },

                async upMove(iitem) {
                   this.$emit('up-move', iitem)
                },

                async downMove(iitem) {
                   this.$emit('down-move', iitem)
                },

                async changeSort(iitem) {
                   this.$emit('change-sort', iitem)
                },
            },
            template: '<ul><li v-for="iitem in item.children"> <span>【{{iitem.dicDescription}}】     @    {{iitem.dicCode}}</span> <input type="button" value="删除" @click="del(iitem);"> <input type="button" value="置顶" @click="upTop(iitem);"> <input type="button" value="上移" @click="upMove(iitem);"> <input type="button" value="下移" @click="downMove(iitem);"> <input type="button" value="交换" @click="changeSort(iitem);"> <input type="button" value="修改" @click="showModify(iitem);"> <tree-item v-if="iitem.children&&iitem.children.length>0" :item="iitem" @del="del" @up-top="upTop" @up-move="upMove" @down-move="downMove" @change-sort="changeSort" @show-modify="showModify"></tree-item></li></ul>'
         }),
      },


    })




</script>
</html>
