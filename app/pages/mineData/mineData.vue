<template>
  <view class="goods-container">
    <view class="goods-box" v-for="(item, index) in list" :key="index">
      <navigator hover-class="none">
        <view class="text-black margin-top text-bold padding-lr overflow-1">{{ item.title }}</view>
        <view class="img-box">
          <image :src="item.photoUrl" class="margin-top-xs" v-if="type == 0 || type == 2"/>
          <image :src="item.files[0]" class="margin-top-xs" v-if="type == 1 || type == 3 ||type == 4"/>
        </view>
        <view class="text-center margin-top">
          <view class="text-gray text-sm padding-lr-sm margin-top-sm" v-if="type == 3  ||type == 4" @click="viewInfo(item.photoCircleId)">查看详情</view>
          <view class="text-gray text-sm padding-lr-sm margin-top-sm" v-if="type == 1 && item.photoCircleId !=null" @click="viewInfo(item.photoCircleId)">查看详情</view>
          <view class="text-gray text-sm padding-lr-sm margin-top-sm" @click="del(item.id,type)">删除</view>
        </view>
      </navigator>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      list: [],
      type:null
    }
  },
  onLoad(a) {
    var that = this
    let e = a.type
    that.type = e
    that.getData()
  },
  methods: {
    getData(){
      var that = this
      uni.request({
        url: that.$serverUrl + 'mineData/'+that.type,
        method: 'GET',
        header: {
          'token': uni.getStorageSync('token'),
          'content-type': 'application/json'
        },
        success: (res) => {
          var data = res.data
          that.list = data.data
          uni.showLoading({
            title: data.msg
          });
          that.dataConfesslist = res.data.data.mineConfess
          that.title = that.type == 0 ? '我的收藏' : that.type == 1 ? '我的点赞' : that.type == 2 ? '我的图片' :  that.type == 3 ? '我的发布' :'我的评论'
          uni.setNavigationBarTitle({
            title: that.title
          })
          setTimeout(function () {
            uni.hideLoading();
          }, 500);
        }
      });
    },
    viewInfo(e) {
      uni.navigateTo({
        url:"../photo/commentList?data="+JSON.stringify(e)
      })
    },
    del(e,a) {
      var that = this
      uni.request({
        url: that.$serverUrl + 'delMineData/'+e+'/'+a,
        method: 'GET',
        header: {
          'token': uni.getStorageSync('token'),
          'content-type': 'application/json'
        },
        success: (res) => {
          that.getData()
          setTimeout(function () {
            uni.hideLoading();
          }, 500);
        }
      });

    },
  }
}
</script>

<style>
.goods-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  box-sizing: content-box;
  padding: 20rpx;
}
.goods-box {
  width: 336rpx;
  height: 560rpx;
  background-color: #fff;
  overflow: hidden;
  margin-top: 40rpx;
  border-radius: 20rpx;
  box-shadow: 0rpx 0rpx 50rpx 10rpx rgba(211, 211, 211, 1);
}
.goods-box .img-box {
  width: 100%;
  height: 349rpx;
  overflow: hidden;
}
.goods-box .img-box image {
  width: 100%;
  height: 336rpx;
}
</style>