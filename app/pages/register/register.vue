<template>
  <form @submit="register">
    <view class="cu-form-group margin-top">
      <view class="title">姓名</view>
      <input placeholder="请输入您的姓名" name="name"></input>
    </view>
    <view class="cu-form-group">
      <view class="title">登录账号</view>
      <input placeholder="请输入您的登录账号" name="num"></input>

    </view>
    <view class="cu-form-group">
      <view class="title">登录密码</view>
      <input placeholder="请输入您的登录密码" name="pwd" type="password"></input>
      <input hidden name="image" :value="image"></input>
    </view>
    <view class="cu-bar bg-white margin-top">
      <view class="action">
        头像
      </view>
      <view class="action">
        {{ imgList.length }}/1
      </view>
    </view>
    <view class="cu-form-group">
      <view class="grid col-4 grid-square flex-sub">
        <view class="bg-img" v-for="(item,index) in imgList" :key="index"
              :data-url="imgList[index]">
          <image :src="imgList[index]" mode="aspectFill"></image>
          <view class="cu-tag bg-red" @tap.stop="changeAvatar" :data-index="index">
            <text class='cuIcon-close'></text>
          </view>
        </view>
        <view class="solids" @tap="changeAvatar" v-if="imgList.length<1">
          <text class='cuIcon-cameraadd'></text>
        </view>
      </view>
    </view>
    <view class="padding flex flex-direction">
      <button form-type="submit" class="cu-btn bg-cyan lg">注册</button>
    </view>
  </form>
</template>

<script>
export default {
  data() {
    return {
      userImageUrl: '',
      imgList: [

      ]
    }
  },
  methods: {
    //注册
    register(e) {
      //请求注册接口
      uni.request({
        url: this.$serverUrl + 'addUser',
        method: 'POST',
        header: {
          'content-type': 'application/json' //自定义请求头信息
        },
        data: {
          'num': e.detail.value.num,
          'pwd': e.detail.value.pwd,
          'name': e.detail.value.name,
          'userImageUrl': this.userImageUrl,
          'type': 0
        },
        success: (res) => {
          var data = res.data
          uni.showLoading({
            title: data.msg
          });
          setTimeout(function () {
            if (data.code == 200) {
              //跳登录页面
              uni.navigateTo({
                url: "../login/login"
              })
            }
            uni.hideLoading();
          }, 500);
        }
      });
    },
    //上传图片
    changeAvatar(e) {
      var that = this
      uni.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ['album', 'camera'], //从相册选择
        success(response) {
          console.log(that.imgList)
          that.imgList = response.tempFilePaths
          console.log(that.imgList)
          let url = response.tempFilePaths[0];
          uni.uploadFile({
            url: that.$serverUrl + 'uploadImgAddUser',
            filePath: url,
            name: 'file',
            success: (res) => {
              var data = JSON.parse(res.data)
              if (data.code != 200) {
                uni.showLoading({
                  title: data.msg
                });
                setTimeout(function () {
                  uni.hideLoading();
                }, 500);
              } else {
                that.userImageUrl = data.data.url
              }
            }
          })
        }

      })

    }
  }
}
</script>

<style>

</style>
