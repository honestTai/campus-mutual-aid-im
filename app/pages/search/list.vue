<template>
	<view class="index">
		<block v-for="(item, index) in list" :key="index">
			<view class="card" >
				<image class="card-img" :src="item.photoUrl" mode="aspectFill"></image>
				<text class="card-num-view card-list2-num-view">{{item.totalCollect}}⭐</text>
				<view class="card-bottm row">
					<view class="car-title-view row">
						<text class="card-title">{{item.tagTitle}}</text>
					</view>
					<view @click.stop="collect(item)" class="card-collect-view"></view>
				</view>
			</view>
		</block>
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
			}
		},
		onLoad(e) {
			console.log(e)
			setTimeout(() => { 
				//防止app里由于渲染导致转场动画卡顿
				this.list = JSON.parse(e.data);
			}, 300)
		},
		methods: {
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
