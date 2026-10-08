<template>
	<view class="index">
		<view class="grid">
			<view class="grid-c-06" v-for="item in list" :key="item.id">
				<view class="panel" @click="goDetail(item)">
					<image class="card-img card-list2-img" :src="item.photoUrl"></image>
					<text class="card-num-view card-list2-num-view">{{item.totalCollect}}⭐</text>
					<view class="card-bottm row">
						<view class="car-title-view row">
							<text class="card-title card-list2-title">{{item.title}}</text>
						</view>
						<view @click.stop="collect(item)" class="card-collect-view"></view>
					</view>
				</view>
			</view>
		</view>
		<text class="loadMore">没有了....</text>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				refreshing: false,
				loadMoreText: "加载中...",
				list: [],
				id: 0,
				fetchPageNum: 1
			}
		},
		onLoad(e) {
			uni.setNavigationBarTitle({
				title: "专题：" + e.title
			})
			this.id = e.id;
			setTimeout(() => { //防止app里由于渲染导致转场动画卡顿
				this.getData();
			}, 300)
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
			getData(e) {
				uni.request({
					url: this.$serverUrl + 'photoListByTag',
					method: 'POST',
					header: {
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'pageSize': 10,
						'pageCurrent': this.fetchPageNum,
						'id': this.id
					},
					success: (ret) => {
						if (ret.statusCode !== 200) {
							console.log("请求失败", ret)
						} else {
							var data = ret.data.data.list
							if (data == "") {
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
			goDetail(e) {
				uni.navigateTo({
					url: "../detail/detail?data=" + encodeURIComponent(JSON.stringify(e))
				})
			},
			collect(e){
				uni.request({
					url: this.$serverUrl + 'collect',
					method: 'POST',
					header: {
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'id': e.id
					},
					success: (res) => {
						var data=res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							uni.hideLoading();
						}, 500);
					}
				});
			}
		}
	}
</script>

<style>

</style>
