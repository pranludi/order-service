# 빌드
custom_build(
  ref = 'order-service',
  command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
  deps = ['build.grade.kts', 'src']
)

# 배포
k8s_yaml(kustomize('k8s'))

# 관리
k8s_resource('order-service', port_forwards=['9002'])
