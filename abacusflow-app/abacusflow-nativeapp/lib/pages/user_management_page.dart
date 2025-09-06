import 'package:abacusflow_openapi/abacusflow_openapi.dart';
import 'package:flutter/material.dart';

//  例如调用用户API
final api = AbacusflowOpenapi().getUserApi();

class UserManagementPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('用户管理')),
      body: UserList(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => UserEditPage()),
          );
        },
        child: const Icon(Icons.add),
        tooltip: '新增用户',
      ),
    );
  }
}

class UserList extends StatelessWidget {
  // 异步获取用户数据的方法
  Future<List<BasicUser>?> _fetchUsers() async {
    try {
      final response = await api.listBasicUsers();
      return response.data ?? [];
    } catch (e) {
      print('获取用户列表失败: $e');
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<BasicUser>?>(
      future: _fetchUsers(), // 异步获取数据
      builder: (context, snapshot) {
        if (!snapshot.hasData || snapshot.data!.isEmpty) {
          // 空数据状态
          return const Center(child: Text('暂无用户数据'));
        }
        final users = snapshot.data!; // 这里定义 users 变量
        return ListView.separated(
          itemCount: users.length,
          separatorBuilder: (context, index) => const Divider(),
          itemBuilder: (context, index) {
            final user = users[index];
            return ListTile(
              title: Text(user.nick),
              subtitle: Text('用户名: ${user.name}'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => UserDetailPage(user: user),
                  ),
                );
              },
            );
          },
        );
      },
    );
  }
}

class UserDetailPage extends StatelessWidget {
  final BasicUser user;

  const UserDetailPage({required this.user, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('用户详情 - ${user.nick}')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('姓名: ${user.nick}', style: TextStyle(fontSize: 18)),
            SizedBox(height: 8),
            Text('用户名: ${user.name}', style: TextStyle(fontSize: 16)),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => UserEditPage(user: user),
                  ),
                );
              },
              child: const Text('编辑用户'),
            ),
          ],
        ),
      ),
    );
  }
}

class UserEditPage extends StatelessWidget {
  final BasicUser? user;
  const UserEditPage({this.user, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final isEdit = user != null;
    final nickController = TextEditingController(text: user?.nick ?? '');
    final nameController = TextEditingController(text: user?.name ?? '');
    return Scaffold(
      appBar: AppBar(title: Text(isEdit ? '编辑用户' : '新增用户')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: nickController,
              decoration: const InputDecoration(labelText: '姓名'),
            ),
            TextField(
              controller: nameController,
              decoration: const InputDecoration(labelText: '用户名'),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                // 保存逻辑
                Navigator.pop(context);
              },
              child: const Text('保存'),
            ),
          ],
        ),
      ),
    );
  }
}
