<template>
	<view class="index">
		<view class="grid">
			<view class="grid-c-06" v-for="item in list" :key="item.id">
				<view class="panel">
					<image class="card-img card-list2-img" :src="item.photoUrl"></image>
					<text class="card-num-view card-list2-num-view">{{item.totalCollect}}⭐</text>
					<view class="card-bottm row">
						<view class="car-title-view row">
							<text class="card-title card-list2-title">{{item.title}}</text>
						</view>
						<view @click.stop="Nocollect(item)" class="card-collect-view"></view>
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
				//图片数组
				list: [],
				fetchPageNum: 1
			}
		},
		onLoad() {
			//执行
			this.getData();
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
			//获取我上传的图片列表
			getData() {
				uni.request({
					url: this.$serverUrl + 'viewMyPhoto',
					method: 'POST',
					header: {
						//token
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'viewType': 1,
						'pageSize': 10,
						'pageCurrent': this.fetchPageNum
					},
					success: (ret) => {
						console.log("data",ret);
						if (ret.statusCode !== 200) {
							console.log("失败!");
						} else {
							var data=ret.data.data.list
							if (data=="" && this.refreshing) {
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
			//删除图片
			deletePhoto(e) {
				var that = this;
				//弹出二次确认模态框
				uni.showModal({
					title: '删除提示',
					content: '确认删除?',
					success: (res) => {
						if (res.confirm) {
							//点击确认进行删除接口访问
							uni.request({
								url: that.$serverUrl + 'del',
								method: 'POST',
								header: {
									//token
									'token': uni.getStorageSync('token'),
									'content-type': 'application/json'
								},
								data: {
									'viewType': 0,
									'photoId': e.id,
									'filePath': e.filePath
								},
								success: (res) => {
									var data = res.data
									uni.showLoading({
										title: data.msg
									});
									setTimeout(function() {
										if (data.code == 200) {
											//刷新
											that.getData();
										}
										uni.hideLoading();
									}, 500);
								},
							})
						}
					}
				})
			},
			//取消收藏
			Nocollect(e){
				var that = this;
					//弹出二次确认模态框
					uni.showModal({
						title: '取消提示',
						content: '确认取消收藏?',
						success: (res) => {
							if (res.confirm) {
								//点击确认进行删除接口访问
								uni.request({
									url: that.$serverUrl + 'del',
									method: 'POST',
									header: {
										//token
										'token': uni.getStorageSync('token'),
										'content-type': 'application/json'
									},
									data: {
										'viewType': 1,
										'photoId': e.id
									},
									success: (res) => {
										var data = res.data
										uni.showLoading({
											title: data.msg
										});
										setTimeout(function() {
											if (data.code == 200) {
												//刷新
												that.getData();
											}
											uni.hideLoading();
										}, 500);
									},
								})
							}
						}
					})
				}
			}
	}
</script>

<style>
	/* .grid {
		padding-top: 10px;
	} */
</style>
