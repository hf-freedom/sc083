import React from 'react';
import { Form, Input, DatePicker, Select, Button, Card, message, Space } from 'antd';
import { userApi } from '../api';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';

const { Option } = Select;
const { TextArea } = Input;

function UserCreate() {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    try {
      const data = {
        name: values.name,
        idCard: values.idCard,
        phone: values.phone,
        gender: values.gender,
        address: values.address,
        birthDate: values.birthDate?.format('YYYY-MM-DD'),
        contraindications: values.contraindications || [],
      };

      const res = await userApi.create(data);

      if (res.success) {
        message.success('用户创建成功');
        navigate('/users');
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to create user:', error);
      message.error('创建用户失败');
    }
  };

  return (
    <div>
      <h2 className="page-header">新增用户</h2>
      
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          style={{ maxWidth: 600 }}
        >
          <Form.Item
            name="name"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" />
          </Form.Item>

          <Form.Item
            name="idCard"
            label="身份证号"
            rules={[{ required: true, message: '请输入身份证号' }]}
          >
            <Input placeholder="请输入身份证号" maxLength={18} />
          </Form.Item>

          <Form.Item
            name="phone"
            label="手机号"
            rules={[
              { required: true, message: '请输入手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
            ]}
          >
            <Input placeholder="请输入手机号" maxLength={11} />
          </Form.Item>

          <Form.Item
            name="gender"
            label="性别"
            rules={[{ required: true, message: '请选择性别' }]}
          >
            <Select placeholder="请选择性别">
              <Option value="男">男</Option>
              <Option value="女">女</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="birthDate"
            label="出生日期"
            rules={[{ required: true, message: '请选择出生日期' }]}
          >
            <DatePicker
              style={{ width: '100%' }}
              placeholder="请选择出生日期"
              format="YYYY-MM-DD"
            />
          </Form.Item>

          <Form.Item
            name="address"
            label="地址"
          >
            <Input placeholder="请输入地址" />
          </Form.Item>

          <Form.Item
            name="contraindications"
            label="禁忌症"
          >
            <Select
              mode="tags"
              placeholder="请输入或选择禁忌症（回车添加）"
              style={{ width: '100%' }}
            >
              <Option value="青霉素过敏">青霉素过敏</Option>
              <Option value="海鲜过敏">海鲜过敏</Option>
              <Option value="哮喘">哮喘</Option>
              <Option value="高血压">高血压</Option>
              <Option value="糖尿病">糖尿病</Option>
              <Option value="心脏病">心脏病</Option>
            </Select>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                创建用户
              </Button>
              <Button onClick={() => navigate('/users')}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default UserCreate;
