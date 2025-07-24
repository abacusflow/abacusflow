import 'package:flutter/material.dart';
import 'pages/dashboard_page.dart';
import 'pages/user_management_page.dart';
import 'pages/inventory_management_page.dart';
import 'pages/transaction_management_page.dart';
import 'pages/product_center_page.dart';
import 'pages/partner_management_page.dart';
import 'pages/storage_point_page.dart';

class AbacusApp extends StatelessWidget {
  const AbacusApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AbacusFlow',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MainNavigation(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MainNavigation extends StatefulWidget {
  const MainNavigation({Key? key}) : super(key: key);

  @override
  State<MainNavigation> createState() => _MainNavigationState();
}

class _MainNavigationState extends State<MainNavigation> {
  int _selectedIndex = 0;
  static final List<Widget> _pages = <Widget>[
    DashboardPage(),
    UserManagementPage(),
    InventoryManagementPage(),
    TransactionManagementPage(),
    ProductCenterPage(),
    PartnerManagementPage(),
    StoragePointPage(),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(icon: Icon(Icons.dashboard), label: '仪表盘'),
          BottomNavigationBarItem(icon: Icon(Icons.people), label: '用户'),
          BottomNavigationBarItem(icon: Icon(Icons.inventory), label: '库存'),
          BottomNavigationBarItem(icon: Icon(Icons.receipt_long), label: '交易'),
          BottomNavigationBarItem(icon: Icon(Icons.category), label: '产品'),
          BottomNavigationBarItem(icon: Icon(Icons.handshake), label: '合作伙伴'),
          BottomNavigationBarItem(icon: Icon(Icons.store), label: '储存点'),
        ],
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
      ),
    );
  }
} 