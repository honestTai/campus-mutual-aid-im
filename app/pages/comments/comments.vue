<template>
	<form class='loginView' @submit="comments">
		<view class="input-view" style="height: 121px;">
			<view class="label-view">
				<text class="label">评论内容</text>
			</view>
			<textarea class="input" type="text" placeholder="请输入评论内容" name="comment" />
			</view>
		<view class="button-view">
			<button type="default" class="login" hover-class="hover" formType="submit">确定</button>
		</view>
	</form>
</template>

<script>
	export default {
		data() {
			return {
				photoCircleId:'',
				comment:''
			};
		},
		onLoad(e) {
			var that=this
			console.log(e.data)
			that.photoCircleId=e.data
		},
		methods: {
			comments(e){
				uni.request({
					url: this.$serverUrl + 'addComment',
					method: 'POST',
					header: {
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'photoCircleId': this.photoCircleId,
						'comment':e.detail.value.comment
					},
					success: (res) => {
						var data=res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							if(data.code==200){
								uni.switchTab({
									url:'../photo/photo'
								})
							}
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
