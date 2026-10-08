<template>
  <view>
    <view class="person-head">
      <cmd-avatar :src="userInfo.userImageUrl" @click="fnInfoWin" size="lg"
                  :make="{'background-color': '#fff'}"></cmd-avatar>
      <view class="person-head-box">
        <view class="person-head-nickname">{{ userInfo.name }}</view>
      </view>
    </view>
    <view class="person-list">
      <cmd-cell-item title="我的收藏" slot-left arrow @click="mineData(0)" :brief="'总个数'+static.totalCollect">
        <cmd-icon type="bullet-list" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
      <cmd-cell-item title="我的点赞" slot-left arrow @click="mineData(1)" :brief="'总个数'+static.totalLike">
        <cmd-icon type="message" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
      <cmd-cell-item title="我的图片内容" slot-left arrow @click="mineData(2)" :brief="'总个数'+static.totalPic">
        <cmd-icon type="settings" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
      <cmd-cell-item title="我的发布" slot-left arrow @click="mineData(3)" :brief="'总个数'+static.totalPo">
        <cmd-icon type="settings" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
      <cmd-cell-item title="我的评论" slot-left arrow @click="mineData(4)" :brief="'总个数'+static.totalComment">
        <cmd-icon type="alert-circle" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
      <cmd-cell-item title="上传图片内容" slot-left arrow @click="uploadPhoto()" >
        <cmd-icon type="alert-circle" size="24" color="#368dff"></cmd-icon>
      </cmd-cell-item>
    </view>
    <view class="padding flex flex-direction">
      <button form-type="submit" class="cu-btn bg-cyan lg" @click="removeLogin()">退出登录</button>
    </view>
  </view>

</template>

<script>
import cmdAvatar from "@/components/cmd-avatar/cmd-avatar.vue"
import cmdIcon from "@/components/cmd-icon/cmd-icon.vue"
import cmdCellItem from "@/components/cmd-cell-item/cmd-cell-item.vue"

export default {
  components: {
    cmdAvatar,
    cmdCellItem,
    cmdIcon
  },
  data() {
    return {
      login: false,
      avatarUrl: "../../static/logo.png",
      userInfo: {},
      static: {}
    }
  },
  onShow: function (options) {
    var that = this
    that.getStaticData()
    uni.showLoading({
      title: "页面刷新中"
    });
    setTimeout(function () {
      var userInfo = (uni.getStorageSync('userInfo'))
      //判断是否有缓存
      if (userInfo != "") {
        console.log(userInfo)
        //取值绑值
        that.userInfo = userInfo
        that.login = true
      } else {
        uni.showLoading({
          title: "请登录"
        });
        setTimeout(function () {
          uni.navigateTo({
            url: "../login/login"
          })
        }, 500);
        that.login = false
      }
      uni.hideLoading();
    }, 500);
  },
  methods: {
    uploadPhoto(){
      uni.navigateTo({
        url: '../upload/upload'
      })
    },
    mineData(e) {
      uni.navigateTo({
        url: '../mineData/mineData?type=' + e
      })
    },
    getStaticData() {
      let that = this
      uni.request({
        url: that.$serverUrl + 'staticCount',
        method: 'GET',
        header: {
          'token': uni.getStorageSync('token'),
          'content-type': 'application/json' //自定义请求头信息
        },
        success: (res) => {
          var data = res.data
          uni.showLoading({
            title: data.msg
          });
          that.static = data.data
          setTimeout(function () {
            uni.hideLoading();
          }, 500);
        }
      });
    },
    //退出登录
    removeLogin() {
      var that = this
      uni.showLoading({
        title: "退出登录成功"
      });
      setTimeout(function () {
        uni.removeStorageSync("token")
        uni.removeStorageSync("userInfo")
        //刷新当前页面
        that.userInfo = {}
        that.login = false
        uni.hideLoading();
      }, 500);
    },
    goLogin() {
      if (!this.login) {
        uni.navigateTo({
          url: "../login/login"
        })
      } else {
        //修改个人信息
        uni.navigateTo({
          url: "../update/update"
        })
      }
    }
  }
}
</script>

<style>
.person-head {
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 150px;
  padding-left: 20px;
  background: linear-gradient(to right, #365fff, #36bbff);
}

.person-head-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  margin-left: 10px;
}

.person-head-nickname {
  font-size: 18px;
  font-weight: 500;
  color: #fff;
}

.person-head-username {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.person-list {
  line-height: 0;
}
</style>
