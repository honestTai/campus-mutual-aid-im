<template>
	<view class="index">
		<swiper @change="swpierChange" :style="{height:screenHeight + 'px'}">
			<swiper-item v-for="(value,index) in data" :key="index" @click="preImg(index)">
				<image :src="value.photoUrl" mode="widthFix"></image>
			</swiper-item>
		</swiper>
        <!-- #ifndef H5 -->
        <view class="detail-btn-view">
        	<view class="download" @click="download"></view>
        	<!-- #ifdef APP-PLUS -->
        	<view v-if="showBtn" class="setting" @click="setting">设为壁纸</view>
        	<!-- #endif -->
        	<view class="collect" @click="collect"></view>
        </view>
        <!-- #endif -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				imgShow: false,
				index: 0,
				showBtn: false,
				screenHeight: 0,
				imgLength: 0,
				providerList: [],
				data: [],
				detailDec:""
			}
		},
		onLoad(e) {
			let data = JSON.parse(decodeURIComponent(e.data));
			this.getData(data.tagId);
			// #ifdef APP-PLUS
			if (plus.os.name === 'Android') {
				this.showBtn = true;
			}
			// #endif
			this.screenHeight = uni.getSystemInfoSync().windowHeight;
		},
		methods: {
			download() {
				uni.downloadFile({
					url: this.data[this.index].photoUrl,
					success: (e) => {
						uni.saveImageToPhotosAlbum({
							filePath: e.tempFilePath,
							success: () => {
								uni.showToast({
									icon: "none",
									title: "已保存到手机相册"
								})
							},
							fail: () => {
								uni.showToast({
									icon: "none",
									title: "保存到手机相册失败"
								})
							}
						});
					},
					fail: (e) => {
						uni.showModal({
							content: "下载失败，" + e.errMsg,
							showCancel: false
						})
					}
				})
			},
			collect() {
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
							if(data.msg=="请登录"){
								uni.navigateTo({
									url:"../login/login"
								})
							}
							uni.hideLoading();
						}, 500);
					}
				});
			},
			//#ifdef APP-PLUS
			setting() {
				uni.showToast({
					icon: "none",
					title: "正在设为壁纸"
				})
				setTimeout(() => {
					var WallpaperManager = plus.android.importClass("android.app.WallpaperManager");
					var Main = plus.android.runtimeMainActivity();
					var wallpaperManager = WallpaperManager.getInstance(Main);
					plus.android.importClass(wallpaperManager);
					var BitmapFactory = plus.android.importClass("android.graphics.BitmapFactory");
					uni.downloadFile({
						url: this.data[this.index].photoUrl,
						success: (e) => {
							var filePath = e.tempFilePath.replace("file://", "");
							var bitmap = BitmapFactory.decodeFile(filePath);
							try {
								wallpaperManager.setBitmap(bitmap);
								uni.showToast({
									icon: "none",
									title: "壁纸设置成功"
								})
							} catch (e) {
								uni.showToast({
									icon: "none",
									title: "壁纸设置失败"
								})
							}
						},
						fail: () => {
							uni.showToast({
								icon: "none",
								title: "壁纸设置失败"
							})
						}
					})
				}, 100)
			},
			//#endif
			swpierChange(e) {
				this.index = e.detail.current;
				uni.setNavigationBarTitle({
					title: e.detail.current + 1 + "/" + this.imgLength
				})
			},
			preImg(index) {
				if (this.imgShow) { //防止点击过快导致重复调用 
					return;
				}
				this.imgShow = true;
				setTimeout(() => {
					this.imgShow = false;
				}, 1000)
				setTimeout(() => {
					uni.previewImage({
						current: this.data[index],
						urls: this.data[index].photoUrl
					})
				}, 150)
			},
			getData(e) {
				uni.request({
					url: this.$serverUrl + 'photoListByTag',
					method: 'POST',
					header: {
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'id': e,
						'pageCurrent':1,
						'pageSize':10000
					},
					success: (res) => {
						if (res.data.code !== 200) {
							uni.showModal({
								content: "请求失败，失败原因：" + res.data.msg,
								showCancel: false
							})
							return;
						}

						this.data =res.data.data.list;
						this.imgLength=res.data.data.list.length
						uni.setNavigationBarTitle({
							title: "1/" + this.imgLength
						});
					},
					fail: () => {
						uni.showModal({
							content: "请求失败，请重试!",
							showCancel: false
						})
					}
				})
			}
		}
	}
</script>

<style>
	page {
		background-color: #000;
	}

	swiper {
		flex: 1;
		width: 750upx;
		background-color: #000;
		display: flex;
		flex-direction: column;
	}

	swiper-item {
		display: flex;
		align-items: center;
	}

	image {
		width: 750upx;
		height: 1125upx;
	}
</style>
