<template>
  <view style="height: 100%">
    <view class="cu-bar search bg-white fixed" style="box-shadow: none">
      <view class="search-form round">
        <text class="cuIcon-search"></text>
        <input type="text" placeholder="请输入查询内容" confirm-type="search" @confirm="searchHandle" focus/>
      </view>
    </view>
    <view class="margin-top-bar bg-white">
      <view class="cu-bar bg-white solid-bottom margin-top">
        <view class="action">
          <button class="cu-btn bg-green shadow left:-10px" @tap="showModal" data-target="gridModal">论坛发布</button>
        </view>
      </view>

      <photo-card :list="list" :type="true" v-on:getData="refreshData"/>

    </view>
  </view>
</template>
<script>
export default {
  data() {
    return {
      list: [],
      fetchPageNum: 1,
      hidden: true,
      refreshing:false,
      title:null,
    }
  },
  onShow: function() {
    var that = this
    uni.showLoading({
      title: "请等待"
    });
    setTimeout(function() {
      that.fetchPageNum=1
      that.list=[]
      that.getData();
      uni.hideLoading();
    }, 500);
  },
  onReachBottom() {
    console.log("滑动到页面底部")
    this.getData();
  },
  onPullDownRefresh() {
    console.log("下拉刷新");
    this.refreshing = true;
    this.getData();
  },
  methods: {
    showModal(e) {
      uni.navigateTo({
        url:"../photo/addPhotoCircle"
      })
    },
    searchHandle(e) {
      this.title = e.detail.value
      this.list = []
      this.fetchPageNum = 1
      this.getData()
    },
    getData() {
      uni.request({
        url: this.$serverUrl + 'photoCircleList',
        method: 'POST',
        header: {
          'content-type': 'application/json' //自定义请求头信息
        },
        data: {
          'pageSize': 10,
          'pageCurrent': this.fetchPageNum,
          'title': this.title
        },
        success: (ret) => {
          console.log("data", ret);
          if (ret.statusCode !== 200) {
            console.log("失败!");
          } else {
            var data = ret.data.data.list
            if (data == "" && this.refreshing) {
              uni.showToast({
                title: "已经最新",
                icon: "none",
              })
              this.refreshing = false;
              uni.stopPullDownRefresh();
              return;
            }
            if (this.refreshing) {
              this.refreshing = false;
              uni.stopPullDownRefresh()
              this.list = data;
              this.fetchPageNum = 2;
            } else {
              this.list = this.list.concat(data);
              this.fetchPageNum += 1;
            }

          }
        }
      });
    },
    //查看评论
    viewComment(e){
      //评论列表页面
      uni.navigateTo({
        url:"../photo/commentList?data="+JSON.stringify(e)
      })
    },
    refreshData(e){
      this.fetchPageNum = 1
      this.list = []
      this.getData()
    }
  }
}
</script>

<style>
.list-item {
  background-color: #fff;
}

.wrapper-list {
  white-space: nowrap;
  padding: 0rpx 20rpx 50rpx 0rpx;
}

.wrapper-list .item {
  display: inline-block;
  width: 560rpx;
  height: 800rpx;
  margin: 60rpx 0 60rpx 50rpx;
  padding: 10rpx 30rpx;
  border-radius: 25rpx;
  box-shadow: 0rpx 0rpx 50rpx 10rpx rgba(211, 211, 211, 1);
}

.wrapper-list .item:nth-last-child(1) {
  margin-right: 20rpx;
}

.wrapper-list .item .img-box {
  width: 100%;
  height: 480rpx;
}

.wrapper-list .item .img-box image {
  width: 100%;
  height: 100%;
}

.adsec {
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  padding: 7rpx 10rpx;
  height: 80rpx;
}

.adsec-icon {
  height: 80rpx;
  line-height: 80rpx;
}

.swiper_container {
  height: 80rpx;
  width: 95%;
  line-height: 80rpx;
}

.screen-swiper {
  height: 600rpx;
  margin-top: 100rpx;
}

.goods-image {
  width: 100%;
  height: 1150rpx;
}

.hot-goods {
  width: auto;
  overflow: hidden;
}

.goods-buy {
  margin-left: 500rpx;
  margin-top: -120rpx;
  padding-bottom: 50rpx;
}

.buy-now {
  background-color: #2967ff !important;
  font-weight: 300;
  width: 220rpx;
}
</style>
