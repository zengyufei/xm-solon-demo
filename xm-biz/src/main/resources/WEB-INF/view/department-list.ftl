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
    <h1>${title}</h1>
    <h1><a href="/dict/view/list">字典列表页</a></h1>
    <h1><a href="/dict/view/tree">字典树页</a></h1>
</div>
<div id="app">
    <div>
        <input v-show="!isShowAddForm &&!isShowModify&&!isChangeSort" type="button" value="新增" @click="showAddForm">
        <input v-show="isShowAddForm ||isShowModify||isChangeSort" type="button" value="关闭" @click="hideAll">
    </div>

    <div v-show="isShowAddForm">
        <div>
            <span>上级部门:</span>
            <select v-model="form.parentCode">
                <option value="-1">请选择</option>
                <option v-for="item in list" :value="item.code">{{item.name}}</option>
            </select>
        </div>
        <div>
            <span>部门名:</span>
            <input type="text" v-model="form.name"/>
        </div>
        <div>
            <span>部门编码:</span>
            <input type="text" v-model="form.code"/>
        </div>
        <div>
            <span>部门值:</span>
            <input type="text" v-model="form.dicValue" />
        </div>
        <div>
            <input type="button" value="提交" @click="save"/>
        </div>
    </div>

    <div v-if="isChangeSort">
        <span>交换对象:</span>
        <select v-model="targetId" :value="targetId" @change="changeSortConfirm">
            <option value="-1">请选择</option>
            <option v-for="item in changeList" :value="item.id">{{item.name}}</option>
        </select>
    </div>

    <div v-if="isShowModify">
        <div>
            <span>上级部门:</span>
            <select v-model="updateForm.parentCode" :value="updateForm.parentCode">
                <option value="-1">请选择</option>
                <option v-for="item in list.filter(s=>s.code!==updateForm.code)" :value="item.code">{{item.name}}</option>
            </select>
        </div>
        <div>
            <span>部门名:</span>
            <input type="text" v-model="updateForm.name" />
        </div>
        <div>
            <span>部门编码:</span>
            <input type="text" v-model="updateForm.code" />
        </div>
        <div>
            <span>部门值:</span>
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

    <div>
        <ul>
            <li v-for="item in treeList">
                <span>&nbsp;&nbsp;</span>
                <span>&nbsp;&nbsp;</span>
                <span>【{{item.name}}】     @    {{item.code}}</span>
                <span>&nbsp;&nbsp;</span>
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
                <input type="button" value="修改" @click="showModify(item, list);">

                <treeitem v-if="item.children&&item.children.length>0"
                          :item="item"
                          @del="del"
                          @up-top="upTop"
                          @up-move="upMove"
                          @down-move="downMove"
                          @change-sort="changeSort"
                          @show-modify="showModify"></treeitem>
            </li>
        </ul>

    </div>
</div>
</body>
<script>
    var app = new Vue({
      el: '#app',
      props:[],
      data: {
        query: {
            parentCode: '',
            name: '',
            code: '',
            dicValue: '',
        },
        form: {
            parentCode: '-1',
            name: '',
            code: '',
            dicValue: '',
        },
        updateForm: {
            id: '',
            parentCode: '-1',
            name: '',
            code: '',
            dicValue: '',
        },
        list: [],
        treeList: [],
        thisCeng: [],

        successMsg: '',
        errorMsg: '',

        isShowAddForm: false,
        isShowModify: false,
        isChangeSort: false,
        source: {
            parentCode: '-1',
        },
        targetId: '',
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

        async getList() {
            let res = await api({
                method: 'post',
                url:"/department/list",
                headers: {
                  "Content-Type": "application/json",
                },
                data: this.query,
            })
            if (res) {
                // this.showMsg('查询成功!')
                let dataList = res.data
                this.list = res.data
                // console.log(dataList)
                // 找出根节点
                let root = dataList.filter( temp => !dataList.find(item => item.code === temp.parentCode) )
                root.sort((a,b)=> {
                    return a.sort-b.sort
                })

                const getChilds = newlist => {
                    return newlist.map(item => {
                        let children = dataList.filter(temp => temp.parentCode === item.code)
                        children.sort((a,b)=> {
                            return a.sort-b.sort
                        })
                        item.children = getChilds(children)
                        return item
                    })
                }
                this.treeList = getChilds(root)
            }
        },

        async save() {
            let res = await api({
                method: 'post',
                url:"/department/add",
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

        async update() {
            let res = await api({
                method: 'post',
                url:"/department/update",
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


        showAddForm() {
           this.isShowAddForm = true
           this.isShowModify = false
           this.isChangeSort = false
        },


        showModify(item, list) {
           this.isShowAddForm = false
           this.isShowModify = true
           this.isChangeSort = false
           this.updateForm = JSON.parse(JSON.stringify(item))
           this.thisCeng = list.filter(s=>s.id!==item.id)
        },

        async del(item) {
            let res = await api({
                method: 'post',
                url:"/department/del",
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


        async upTop(item) {
            let res = await api({
                method: 'post',
                url:"/department/upTop",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentCode: item.parentCode},
            })
            if (res) {
                this.showMsg('置顶成功!')
                await this.getList()
            }
        },

        async upMove(item) {
            let res = await api({
                method: 'post',
                url:"/department/upMove",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentCode: item.parentCode},
            })
            if (res) {
                this.showMsg('上移成功!')
                await this.getList()
            }
        },

        async downMove(item) {
            let res = await api({
                method: 'post',
                url:"/department/downMove",
                headers: {
                  "Content-Type": "application/json",
                },
                data: {id:item.id, parentCode: item.parentCode},
            })
            if (res) {
                this.showMsg('下移成功!')
                await this.getList()
            }
        },

        async changeSort(item) {
            this.isShowAddForm = false
            this.isChangeSort = true
            this.isShowModify = false
            this.source = item
        },


        async changeSortConfirm() {
            let res = await api({
                method: 'post',
                url:"/department/changeSort?preId=" + this.source.id + "&nextId=" + this.targetId,
                headers: {
                    "Content-Type": "application/json",
                },
                data: {preId: this.source.id, nextId: this.targetId},
            })
            if (res) {
                this.showMsg('交换成功!')
                this.hideAll()
                await this.getList()
            }
        },

        showMsg(msg) {
            this.successMsg = msg
            this.cleanMsg()
        },

        cleanMsg() {
            setTimeout(() => {
                this.successMsg = ''
            }, 1000)
        },

        hideAll() {
            this.isShowAddForm = false
            this.isChangeSort = false
            this.isShowModify = false
        },

      },
      computed: {
          changeList() {
            return this.list.filter(s=>s.code!==this.source.code)
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
                showModify(iitem, list) {
                console.log('list', list)
                   this.$emit('show-modify', iitem, list)
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
            template: '<ul><li v-for="iitem in item.children"> <span>【{{iitem.name}}】     @    {{iitem.code}}</span> <input type="button" value="删除" @click="del(iitem);"> <input type="button" value="置顶" @click="upTop(iitem);"> <input type="button" value="上移" @click="upMove(iitem);"> <input type="button" value="下移" @click="downMove(iitem);"> <input type="button" value="交换" @click="changeSort(iitem);"> <input type="button" value="修改" @click="showModify(iitem, item.children);"> <tree-item v-if="iitem.children&&iitem.children.length>0" :item="iitem" @del="del" @up-top="upTop" @up-move="upMove" @down-move="downMove" @change-sort="changeSort" @show-modify="showModify"></tree-item></li></ul>'
         }),
      },


    })

</script>
</html>
