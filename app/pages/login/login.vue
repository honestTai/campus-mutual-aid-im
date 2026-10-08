<template>
  <view class="container">
    <view class="  flex-wrap padding ">
      <form @submit="login">
        <view class="cu-form-group margin-top">
          <view class="title">账号</view>
          <input placeholder="请输入账号" name="num"></input>
        </view>
        <view class="cu-form-group margin-top">
          <view class="title">密码</view>
          <input required="required" type="password" placeholder="请输入密码" name="pwd"></input>
        </view>
        <view class="padding flex flex-direction">
          <button form-type="submit" class="cu-btn bg-cyan lg">登录</button>
        </view>
      </form>
      <view class="padding flex flex-direction">
        <button form-type="submit" class="cu-btn bg-cyan lg" @click="register()">注册</button>
      </view>
    </view>
  </view>

</template>

<script>
export default {
  data() {
    return {};
  },
  methods: {
    login(e) {
      //请求登录接口
      uni.request({
        url: this.$serverUrl + 'login',
        method: 'POST',
        header: {
          'content-type': 'application/json' //自定义请求头信息
        },
        data: {
          'num': e.detail.value.num,
          'pwd': e.detail.value.pwd
        },
        success: (res) => {
          var data = res.data
          uni.showLoading({
            title: data.msg
          });
          setTimeout(function () {
            if (data.code == 200) {
              //存入缓存,并跳转到系统个人中心页面
              //jwt缓存
              uni.setStorageSync('token', data.data.loginUserCode)
              //用户基本信息缓存
              uni.setStorageSync('userInfo', data.data.userInfo)
              //跳转到个人中心页面
              uni.switchTab({
                url: "../new/new"
              })
            }
            uni.hideLoading();
          }, 500);
        }
      });
    },
    register() {
      //跳转到注册页面
      uni.navigateTo({
        url: "../register/register"
      })
    }
  }
}
</script>

<style>

</style>
